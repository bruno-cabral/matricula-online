import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Curso, CursoRequest } from '../models/curso.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly apiUrl = '/api/cursos';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, sort = 'nome,asc', q?: string): Observable<PageResponse<Curso>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (q) {
      params = params.set('q', q);
    }
    return this.http.get<PageResponse<Curso>>(this.apiUrl, { params });
  }

  buscarPorUuid(uuid: string): Observable<Curso> {
    return this.http.get<Curso>(`${this.apiUrl}/${uuid}`);
  }

  criar(request: CursoRequest): Observable<Curso> {
    return this.http.post<Curso>(this.apiUrl, request);
  }

  atualizar(uuid: string, request: CursoRequest): Observable<Curso> {
    return this.http.put<Curso>(`${this.apiUrl}/${uuid}`, request);
  }

  deletar(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${uuid}`);
  }
}
