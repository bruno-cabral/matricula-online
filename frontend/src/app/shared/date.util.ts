/** Normaliza valores da API/formulario para yyyy-MM-dd. */
export function normalizeToIsoDate(value: unknown): string {
  if (value == null || value === '') {
    return '';
  }

  // Jackson às vezes serializa LocalDate como [ano, mês, dia]
  if (Array.isArray(value) && value.length >= 3) {
    const year = Number(value[0]);
    const month = Number(value[1]);
    const day = Number(value[2]);
    if (year && month && day) {
      return toIsoDate(year, month, day);
    }
    return '';
  }

  if (typeof value === 'string') {
    const iso = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);
    if (iso) {
      return `${iso[1]}-${iso[2]}-${iso[3]}`;
    }
    return parseDateBr(value) ?? '';
  }

  return '';
}

/** Converte yyyy-MM-dd (ou ISO) para dd/MM/yyyy sem deslocar fuso. */
export function formatDateBr(value: string | null | undefined): string {
  const iso = normalizeToIsoDate(value);
  if (!iso) {
    return '';
  }
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(iso);
  if (!match) {
    return '';
  }
  return `${match[3]}/${match[2]}/${match[1]}`;
}

/** Converte dd/MM/yyyy para yyyy-MM-dd, ou null se incompleto/inválido. */
export function parseDateBr(value: string | null | undefined): string | null {
  if (!value) {
    return null;
  }
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value.trim());
  if (!match) {
    return null;
  }
  const day = Number(match[1]);
  const month = Number(match[2]);
  const year = Number(match[3]);
  const date = new Date(year, month - 1, day);
  if (
    date.getFullYear() !== year ||
    date.getMonth() !== month - 1 ||
    date.getDate() !== day
  ) {
    return null;
  }
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
}

/** Aplica máscara dd/MM/yyyy enquanto digita. */
export function formatDateBrMask(value: string | null | undefined): string {
  const digits = (value ?? '').replace(/\D/g, '').slice(0, 8);
  if (digits.length <= 2) {
    return digits;
  }
  if (digits.length <= 4) {
    return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  }
  return `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4)}`;
}

export function toIsoDate(year: number, month: number, day: number): string {
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
}
