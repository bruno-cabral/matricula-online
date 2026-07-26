import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Matricula, MatriculaRequest } from '../models/matricula.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class MatriculaService {
  private readonly apiUrl = '/api/matriculas';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, status?: string, sort = 'dataMatricula,desc'): Observable<PageResponse<Matricula>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Matricula>>(this.apiUrl, { params });
  }

  buscarPorUuid(uuid: string): Observable<Matricula> {
    return this.http.get<Matricula>(`${this.apiUrl}/${uuid}`);
  }

  criar(request: MatriculaRequest): Observable<Matricula> {
    return this.http.post<Matricula>(this.apiUrl, request);
  }

  confirmar(uuid: string): Observable<Matricula> {
    return this.http.patch<Matricula>(`${this.apiUrl}/${uuid}/confirmar`, {});
  }

  cancelar(uuid: string): Observable<Matricula> {
    return this.http.patch<Matricula>(`${this.apiUrl}/${uuid}/cancelar`, {});
  }

  listarPorAluno(alunoUuid: string, page = 0, size = 10, status?: string, sort = 'dataMatricula,desc'): Observable<PageResponse<Matricula>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Matricula>>(`${this.apiUrl}/aluno/${alunoUuid}`, { params });
  }

  listarPorTurma(turmaUuid: string, page = 0, size = 10, status?: string, sort = 'dataMatricula,desc'): Observable<PageResponse<Matricula>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Matricula>>(`${this.apiUrl}/turma/${turmaUuid}`, { params });
  }
}
