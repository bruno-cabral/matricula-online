export type SortDirection = 'asc' | 'desc';

export interface SortState {
  field: string;
  direction: SortDirection;
}

export function toSortParam(sort: SortState): string {
  return `${sort.field},${sort.direction}`;
}

/** Alterna a ordenação: mesmo campo inverte; novo campo inicia em asc. */
export function toggleSort(current: SortState, field: string): SortState {
  if (current.field === field) {
    return {
      field,
      direction: current.direction === 'asc' ? 'desc' : 'asc'
    };
  }
  return { field, direction: 'asc' };
}

export function sortIndicator(current: SortState, field: string): string {
  if (current.field !== field) {
    return '';
  }
  return current.direction === 'asc' ? ' ▲' : ' ▼';
}
