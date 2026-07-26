import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turma, TurmaRequest } from '../models/turma.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly apiUrl = '/api/turmas';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, sort = 'codigo,asc'): Observable<PageResponse<Turma>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
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
