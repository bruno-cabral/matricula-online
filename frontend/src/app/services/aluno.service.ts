import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Aluno, AlunoRequest } from '../models/aluno.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly apiUrl = '/api/alunos';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, sort = 'nome,asc'): Observable<PageResponse<Aluno>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<PageResponse<Aluno>>(this.apiUrl, { params });
  }

  buscarPorUuid(uuid: string): Observable<Aluno> {
    return this.http.get<Aluno>(`${this.apiUrl}/${uuid}`);
  }

  criar(request: AlunoRequest): Observable<Aluno> {
    return this.http.post<Aluno>(this.apiUrl, request);
  }

  atualizar(uuid: string, request: AlunoRequest): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.apiUrl}/${uuid}`, request);
  }

  deletar(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${uuid}`);
  }
}
