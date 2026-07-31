import {
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  computed,
  effect,
  forwardRef,
  inject,
  input,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Observable, Subject, Subscription, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, finalize, switchMap, tap } from 'rxjs/operators';

export interface SearchableSelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SearchableSelectPage {
  content: SearchableSelectOption[];
  page: number;
  totalPages: number;
}

export type SearchableSelectFetcher = (request: {
  page: number;
  size: number;
  query: string;
}) => Observable<SearchableSelectPage>;

@Component({
  selector: 'app-searchable-select',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchableSelectComponent),
      multi: true
    }
  ],
  template: `
    <div class="searchable-select" [class.open]="open()" [class.disabled]="disabled()">
      <input
        type="text"
        role="combobox"
        [id]="inputId()"
        [attr.aria-expanded]="open()"
        [attr.aria-controls]="listId"
        [placeholder]="placeholder()"
        [disabled]="disabled()"
        [value]="displayValue()"
        (focus)="abrir()"
        (click)="onTriggerClick()"
        (input)="onInput($event)"
        (keydown)="onKeydown($event)"
        autocomplete="off"
      />
      <span class="searchable-select-chevron" aria-hidden="true">▾</span>

      @if (open()) {
        <ul
          [id]="listId"
          class="searchable-select-dropdown"
          role="listbox"
          (scroll)="onScroll($event)"
        >
          @if (options().length === 0 && !loading()) {
            <li class="searchable-select-empty">Nenhum resultado</li>
          } @else {
            @for (opt of options(); track opt.value; let i = $index) {
              <li
                role="option"
                [class.active]="i === highlightedIndex()"
                [class.selected]="opt.value === value()"
                [class.option-disabled]="opt.disabled"
                [attr.aria-selected]="opt.value === value()"
                [attr.aria-disabled]="opt.disabled || null"
                (mousedown)="selecionar(opt, $event)"
                (mouseenter)="highlightedIndex.set(i)"
              >
                {{ opt.label }}
              </li>
            }
          }

          @if (loading()) {
            <li class="searchable-select-status">Carregando...</li>
          } @else if (hasMore() && options().length > 0) {
            <li class="searchable-select-status searchable-select-more" (mousedown)="carregarMais($event)">
              Carregar mais
            </li>
          }
        </ul>
      }
    </div>
  `
})
export class SearchableSelectComponent implements ControlValueAccessor, OnDestroy {
  private readonly host = inject(ElementRef<HTMLElement>);

  fetcher = input.required<SearchableSelectFetcher>();
  selectedLabel = input('');
  placeholder = input('Selecione...');
  inputId = input('');
  pageSize = input(10);

  value = signal('');
  open = signal(false);
  query = signal('');
  disabled = signal(false);
  highlightedIndex = signal(0);
  options = signal<SearchableSelectOption[]>([]);
  loading = signal(false);
  currentPage = signal(0);
  totalPages = signal(0);
  private labelCache = signal('');

  readonly listId = `searchable-list-${Math.random().toString(36).slice(2, 9)}`;

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};
  private readonly search$ = new Subject<string>();
  private searchSub?: Subscription;
  private loadSub?: Subscription;
  private requestSeq = 0;

  hasMore = computed(() => this.currentPage() + 1 < this.totalPages());

  displayValue = computed(() => {
    if (this.open()) {
      return this.query();
    }
    const selected = this.options().find((o: SearchableSelectOption) => o.value === this.value());
    return selected?.label || this.labelCache() || this.selectedLabel() || '';
  });

  constructor() {
    this.searchSub = this.search$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      tap((query: string) => {
        this.query.set(query);
        this.currentPage.set(0);
        this.options.set([]);
        this.highlightedIndex.set(0);
      }),
      switchMap((query: string) => this.fetchPage(0, query, false))
    ).subscribe();

    effect(() => {
      const label = this.selectedLabel();
      if (label) {
        this.labelCache.set(label);
      }
    });
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
    this.loadSub?.unsubscribe();
  }

  writeValue(value: string | null): void {
    this.value.set(value ?? '');
    this.query.set('');
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  onTriggerClick(): void {
    if (this.disabled() || this.open()) {
      return;
    }
    this.abrir();
  }

  abrir(): void {
    if (this.disabled()) {
      return;
    }
    this.open.set(true);
    this.query.set('');
    this.currentPage.set(0);
    this.options.set([]);
    this.highlightedIndex.set(0);
    this.loadSub?.unsubscribe();
    this.loadSub = this.fetchPage(0, '', false).subscribe();
  }

  onInput(event: Event): void {
    const text = (event.target as HTMLInputElement).value;
    this.open.set(true);
    this.search$.next(text);
  }

  onScroll(event: Event): void {
    const el = event.target as HTMLElement;
    if (el.scrollTop + el.clientHeight >= el.scrollHeight - 32) {
      this.carregarMais();
    }
  }

  carregarMais(event?: Event): void {
    event?.preventDefault();
    if (this.loading() || !this.hasMore()) {
      return;
    }
    const next = this.currentPage() + 1;
    this.loadSub?.unsubscribe();
    this.loadSub = this.fetchPage(next, this.query(), true).subscribe();
  }

  onKeydown(event: KeyboardEvent): void {
    if (this.disabled()) {
      return;
    }

    const opts = this.options();

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (!this.open()) {
          this.abrir();
        } else if (opts.length > 0) {
          const next = Math.min(this.highlightedIndex() + 1, opts.length - 1);
          this.highlightedIndex.set(next);
          if (next >= opts.length - 3) {
            this.carregarMais();
          }
          this.scrollHighlightedIntoView();
        }
        break;
      case 'ArrowUp':
        event.preventDefault();
        if (!this.open()) {
          this.abrir();
        } else {
          this.highlightedIndex.set(Math.max(this.highlightedIndex() - 1, 0));
          this.scrollHighlightedIntoView();
        }
        break;
      case 'Enter':
        event.preventDefault();
        if (this.open() && opts[this.highlightedIndex()]) {
          this.selecionar(opts[this.highlightedIndex()]);
        } else {
          this.abrir();
        }
        break;
      case 'Escape':
        event.preventDefault();
        this.fechar();
        break;
      case 'Tab':
        this.fechar();
        break;
    }
  }

  selecionar(opt: SearchableSelectOption, event?: Event): void {
    event?.preventDefault();
    if (opt.disabled) {
      return;
    }
    this.value.set(opt.value);
    this.labelCache.set(opt.label);
    this.query.set('');
    this.onChange(opt.value);
    this.onTouched();
    this.open.set(false);
  }

  @HostListener('document:mousedown', ['$event'])
  onDocumentMouseDown(event: MouseEvent): void {
    if (!this.host.nativeElement.contains(event.target as Node)) {
      this.fechar(true);
    }
  }

  private fetchPage(page: number, query: string, append: boolean): Observable<SearchableSelectPage> {
    const seq = ++this.requestSeq;
    this.loading.set(true);

    return this.fetcher()({ page, size: this.pageSize(), query }).pipe(
      tap((result: SearchableSelectPage) => {
        if (seq !== this.requestSeq) {
          return;
        }
        this.currentPage.set(result.page);
        this.totalPages.set(result.totalPages);
        this.options.set(append ? [...this.options(), ...result.content] : result.content);
        if (!append) {
          this.highlightedIndex.set(this.indexOfSelected());
          this.scrollHighlightedIntoView();
        }
      }),
      catchError(() => {
        if (seq === this.requestSeq && !append) {
          this.options.set([]);
          this.totalPages.set(0);
        }
        return of({ content: [], page, totalPages: 0 });
      }),
      finalize(() => {
        if (seq === this.requestSeq) {
          this.loading.set(false);
        }
      })
    );
  }

  private scrollHighlightedIntoView(): void {
    const index = this.highlightedIndex();
    setTimeout(() => {
      const items = this.host.nativeElement.querySelectorAll(
        '.searchable-select-dropdown li[role="option"]'
      );
      const active = items.item(index) as HTMLElement | null;
      active?.scrollIntoView({ block: 'nearest' });
    });
  }

  private fechar(fromOutside = false): void {
    if (!this.open()) {
      return;
    }
    this.open.set(false);
    this.query.set('');
    if (fromOutside) {
      this.onTouched();
    }
  }

  private indexOfSelected(): number {
    const idx = this.options().findIndex((o: SearchableSelectOption) => o.value === this.value());
    return idx >= 0 ? idx : 0;
  }
}
