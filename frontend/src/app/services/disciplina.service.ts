import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Disciplina, DisciplinaRequest } from '../models/disciplina.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class DisciplinaService {
  private readonly apiUrl = '/api/disciplinas';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, sort = 'nome,asc'): Observable<PageResponse<Disciplina>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<PageResponse<Disciplina>>(this.apiUrl, { params });
  }

  buscarPorUuid(uuid: string): Observable<Disciplina> {
    return this.http.get<Disciplina>(`${this.apiUrl}/${uuid}`);
  }

  criar(request: DisciplinaRequest): Observable<Disciplina> {
    return this.http.post<Disciplina>(this.apiUrl, request);
  }

  atualizar(uuid: string, request: DisciplinaRequest): Observable<Disciplina> {
    return this.http.put<Disciplina>(`${this.apiUrl}/${uuid}`, request);
  }

  deletar(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${uuid}`);
  }
}
