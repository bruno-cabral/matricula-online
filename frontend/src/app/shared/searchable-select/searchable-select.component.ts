import {
  Component,
  ElementRef,
  HostListener,
  forwardRef,
  input,
  signal,
  computed,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export interface SearchableSelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

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
        <ul [id]="listId" class="searchable-select-dropdown" role="listbox">
          @if (filteredOptions().length === 0) {
            <li class="searchable-select-empty">Nenhum resultado</li>
          } @else {
            @for (opt of filteredOptions(); track opt.value; let i = $index) {
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
        </ul>
      }
    </div>
  `
})
export class SearchableSelectComponent implements ControlValueAccessor {
  private readonly host = inject(ElementRef<HTMLElement>);

  options = input<SearchableSelectOption[]>([]);
  placeholder = input('Selecione...');
  inputId = input('');

  value = signal('');
  open = signal(false);
  query = signal('');
  disabled = signal(false);
  highlightedIndex = signal(0);

  readonly listId = `searchable-list-${Math.random().toString(36).slice(2, 9)}`;

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  filteredOptions = computed(() => {
    const q = this.normalize(this.query());
    const opts = this.options();
    if (!q) {
      return opts;
    }
    return opts.filter(o => this.normalize(o.label).includes(q));
  });

  displayValue = computed(() => {
    if (this.open()) {
      return this.query();
    }
    const selected = this.options().find(o => o.value === this.value());
    return selected?.label ?? '';
  });

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

  /** Clique no campo já focado (focus não dispara de novo) — reabre a lista. */
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
    this.highlightedIndex.set(this.indexOfSelected());
    this.scrollHighlightedIntoView();
  }

  onInput(event: Event): void {
    const text = (event.target as HTMLInputElement).value;
    this.query.set(text);
    this.open.set(true);
    this.highlightedIndex.set(0);
    this.scrollHighlightedIntoView();
  }

  onKeydown(event: KeyboardEvent): void {
    if (this.disabled()) {
      return;
    }

    const filtered = this.filteredOptions();

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (!this.open()) {
          this.abrir();
        } else if (filtered.length > 0) {
          this.highlightedIndex.set(Math.min(this.highlightedIndex() + 1, filtered.length - 1));
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
        if (this.open() && filtered[this.highlightedIndex()]) {
          this.selecionar(filtered[this.highlightedIndex()]);
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

  private scrollHighlightedIntoView(): void {
    const index = this.highlightedIndex();
    // Aguarda o Angular atualizar o DOM (abrir lista / classe active)
    setTimeout(() => {
      const items = this.host.nativeElement.querySelectorAll(
        '.searchable-select-dropdown li[role="option"]'
      );
      const active = items.item(index) as HTMLElement | null;
      active?.scrollIntoView({ block: 'nearest' });
    });
  }

  selecionar(opt: SearchableSelectOption, event?: Event): void {
    event?.preventDefault();
    if (opt.disabled) {
      return;
    }
    this.value.set(opt.value);
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
    const idx = this.filteredOptions().findIndex(o => o.value === this.value());
    return idx >= 0 ? idx : 0;
  }

  private normalize(text: string): string {
    return text
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }
}
