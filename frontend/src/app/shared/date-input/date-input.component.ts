import {
  Component,
  ElementRef,
  HostListener,
  computed,
  forwardRef,
  inject,
  input,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import {
  formatDateBr,
  formatDateBrMask,
  normalizeToIsoDate,
  parseDateBr,
  toIsoDate
} from '../date.util';

const DIAS_SEMANA = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
const MESES = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

interface CalendarDay {
  day: number;
  iso: string;
  currentMonth: boolean;
  selected: boolean;
  today: boolean;
}

@Component({
  selector: 'app-date-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateInputComponent),
      multi: true
    }
  ],
  template: `
    <div class="date-input" [class.open]="open()" [class.disabled]="disabled()">
      <input
        type="text"
        inputmode="numeric"
        [id]="inputId()"
        [placeholder]="placeholder()"
        [disabled]="disabled()"
        [value]="displayValue()"
        maxlength="10"
        autocomplete="off"
        (focus)="abrir()"
        (click)="onTriggerClick()"
        (input)="onInput($event)"
        (keydown)="onKeydown($event)"
        (blur)="onBlur()"
      />
      <button
        type="button"
        class="date-input-toggle"
        tabindex="-1"
        [disabled]="disabled()"
        (mousedown)="$event.preventDefault()"
        (click)="toggle()"
        aria-label="Abrir calendário"
      >
        <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
          <path fill="currentColor" d="M7 2h2v2h6V2h2v2h3a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h3V2zm12 8H5v10h14V10zm0-4H5v2h14V6z"/>
        </svg>
      </button>

      @if (open()) {
        <div class="date-calendar" (mousedown)="$event.preventDefault()">
          <div class="date-calendar-header">
            <button type="button" class="date-nav" (click)="navegarMes(-1)" aria-label="Mês anterior">‹</button>
            <div class="date-calendar-title">
              <select
                [ngModel]="viewMonth()"
                (ngModelChange)="alterarMes($event)"
                [ngModelOptions]="{standalone: true}"
                aria-label="Mês"
              >
                @for (mes of meses; track $index) {
                  <option [ngValue]="$index">{{ mes }}</option>
                }
              </select>
              <select
                [ngModel]="viewYear()"
                (ngModelChange)="alterarAno($event)"
                [ngModelOptions]="{standalone: true}"
                aria-label="Ano"
              >
                @for (ano of anos(); track ano) {
                  <option [ngValue]="ano">{{ ano }}</option>
                }
              </select>
            </div>
            <button type="button" class="date-nav" (click)="navegarMes(1)" aria-label="Próximo mês">›</button>
          </div>

          <div class="date-weekdays">
            @for (dia of diasSemana; track dia) {
              <span>{{ dia }}</span>
            }
          </div>

          <div class="date-days">
            @for (d of calendarDays(); track d.iso + '-' + d.currentMonth) {
              <button
                type="button"
                class="date-day"
                [class.outside]="!d.currentMonth"
                [class.selected]="d.selected"
                [class.today]="d.today"
                (click)="selecionarIso(d.iso)"
              >{{ d.day }}</button>
            }
          </div>

          <div class="date-calendar-footer">
            <button type="button" class="btn btn-outline btn-sm" (click)="selecionarHoje()">Hoje</button>
            <button type="button" class="btn btn-outline btn-sm" (click)="limpar()">Limpar</button>
          </div>
        </div>
      }
    </div>
  `
})
export class DateInputComponent implements ControlValueAccessor {
  private readonly host = inject(ElementRef<HTMLElement>);

  inputId = input('');
  placeholder = input('dd/mm/aaaa');

  readonly diasSemana = DIAS_SEMANA;
  readonly meses = MESES;

  value = signal(''); // yyyy-MM-dd
  text = signal(''); // dd/mm/aaaa while editing
  open = signal(false);
  disabled = signal(false);
  viewYear = signal(new Date().getFullYear());
  viewMonth = signal(new Date().getMonth());

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  displayValue = computed(() => {
    if (this.open()) {
      return this.text();
    }
    return formatDateBr(this.value()) || this.text();
  });

  anos = computed(() => {
    const atual = new Date().getFullYear();
    const selecionado = this.viewYear();
    const inicio = Math.min(selecionado, atual) - 100;
    const fim = Math.max(selecionado, atual) + 20;
    const list: number[] = [];
    for (let y = inicio; y <= fim; y++) {
      list.push(y);
    }
    return list;
  });

  calendarDays = computed((): CalendarDay[] => {
    const year = this.viewYear();
    const month = this.viewMonth();
    const first = new Date(year, month, 1);
    const startWeekday = first.getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const prevDays = new Date(year, month, 0).getDate();
    const selected = this.value();
    const now = new Date();
    const today = toIsoDate(now.getFullYear(), now.getMonth() + 1, now.getDate());

    const cells: CalendarDay[] = [];

    for (let i = startWeekday - 1; i >= 0; i--) {
      const day = prevDays - i;
      const date = new Date(year, month, -i);
      const iso = toIsoDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
      cells.push({
        day,
        iso,
        currentMonth: false,
        selected: iso === selected,
        today: iso === today
      });
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const iso = toIsoDate(year, month + 1, day);
      cells.push({
        day,
        iso,
        currentMonth: true,
        selected: iso === selected,
        today: iso === today
      });
    }

    let nextDay = 1;
    while (cells.length < 42) {
      const date = new Date(year, month + 1, nextDay);
      const iso = toIsoDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
      cells.push({
        day: nextDay,
        iso,
        currentMonth: false,
        selected: iso === selected,
        today: iso === today
      });
      nextDay++;
    }

    return cells;
  });

  writeValue(value: unknown): void {
    const iso = normalizeToIsoDate(value);
    this.value.set(iso);
    this.text.set(formatDateBr(iso));
    this.syncViewFromIso(iso);
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
    if (!this.disabled() && !this.open()) {
      this.abrir();
    }
  }

  toggle(): void {
    if (this.disabled()) {
      return;
    }
    if (this.open()) {
      this.fechar();
    } else {
      this.abrir();
    }
  }

  abrir(): void {
    if (this.disabled()) {
      return;
    }
    this.syncViewFromIso(this.value());
    this.text.set(formatDateBr(this.value()));
    this.open.set(true);
  }

  onInput(event: Event): void {
    const masked = formatDateBrMask((event.target as HTMLInputElement).value);
    this.text.set(masked);
    this.open.set(true);

    const iso = parseDateBr(masked);
    if (iso) {
      this.commit(iso, false);
      this.syncViewFromIso(iso);
    } else if (masked.length === 0) {
      this.commit('', false);
    }
  }

  onBlur(): void {
    this.onTouched();
    const iso = parseDateBr(this.text());
    if (iso) {
      this.commit(iso, true);
      this.text.set(formatDateBr(iso));
    } else if (!this.text()) {
      this.commit('', true);
    } else {
      this.text.set(formatDateBr(this.value()));
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.fechar();
    } else if (event.key === 'Enter') {
      event.preventDefault();
      const iso = parseDateBr(this.text());
      if (iso) {
        this.commit(iso, true);
        this.fechar();
      }
    }
  }

  selecionarIso(iso: string): void {
    this.commit(iso, true);
    this.text.set(formatDateBr(iso));
    this.syncViewFromIso(iso);
    this.fechar();
  }

  selecionarHoje(): void {
    const now = new Date();
    const iso = toIsoDate(now.getFullYear(), now.getMonth() + 1, now.getDate());
    this.selecionarIso(iso);
  }

  limpar(): void {
    this.commit('', true);
    this.text.set('');
    this.fechar();
  }

  navegarMes(delta: number): void {
    const date = new Date(this.viewYear(), this.viewMonth() + delta, 1);
    this.viewYear.set(date.getFullYear());
    this.viewMonth.set(date.getMonth());
  }

  alterarMes(month: number | string): void {
    const m = Number(month);
    if (Number.isInteger(m) && m >= 0 && m <= 11) {
      this.viewMonth.set(m);
    }
  }

  alterarAno(year: number | string): void {
    const y = Number(year);
    if (Number.isInteger(y) && y > 0) {
      this.viewYear.set(y);
    }
  }

  @HostListener('document:mousedown', ['$event'])
  onDocumentMouseDown(event: MouseEvent): void {
    if (!this.host.nativeElement.contains(event.target as Node)) {
      this.fechar(true);
    }
  }

  private syncViewFromIso(iso: string): void {
    if (!iso) {
      return;
    }
    const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(iso);
    if (!match) {
      return;
    }
    this.viewYear.set(Number(match[1]));
    this.viewMonth.set(Number(match[2]) - 1);
  }

  private commit(iso: string, touched: boolean): void {
    this.value.set(iso);
    this.onChange(iso);
    if (touched) {
      this.onTouched();
    }
  }

  private fechar(fromOutside = false): void {
    if (!this.open()) {
      return;
    }
    this.open.set(false);
    this.text.set(formatDateBr(this.value()));
    if (fromOutside) {
      this.onTouched();
    }
  }
}
