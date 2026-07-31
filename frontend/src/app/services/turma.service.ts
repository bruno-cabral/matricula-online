import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turma, TurmaRequest } from '../models/turma.model';
import { PageResponse } from '../models/page.model';

export interface TurmaListFilters {
  status?: string;
  lotada?: boolean;
  q?: string;
}

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly apiUrl = '/api/turmas';

  constructor(private http: HttpClient) {}

  listar(
    page = 0,
    size = 10,
    sort = 'codigo,asc',
    filters: TurmaListFilters = {}
  ): Observable<PageResponse<Turma>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.lotada) {
      params = params.set('lotada', 'true');
    }
    if (filters.q) {
      params = params.set('q', filters.q);
    }

    return this.http.get<PageResponse<Turma>>(this.apiUrl, { params });
  }

  buscarPorUuid(uuid: string): Observable<Turma> {
    return this.http.get<Turma>(`${this.apiUrl}/${uuid}`);
  }

  criar(request: TurmaRequest): Observable<Turma> {
    return this.http.post<Turma>(this.apiUrl, request);
  }

  atualizar(uuid: string, request: TurmaRequest): Observable<Turma> {
    return this.http.put<Turma>(`${this.apiUrl}/${uuid}`, request);
  }

  deletar(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${uuid}`);
  }
}
