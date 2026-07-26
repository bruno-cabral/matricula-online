import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AlunoService } from '../../../services/aluno.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { Aluno } from '../../../models/aluno.model';
import { PageResponse } from '../../../models/page.model';
import { SortState, sortIndicator, toggleSort, toSortParam } from '../../../shared/sort.util';

@Component({
  selector: 'app-aluno-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Alunos</h2>
      <a routerLink="/alunos/novo" class="btn btn-primary">+ Novo Aluno</a>
    </div>

    <div class="card">
      @if (page(); as p) {
        @if (p.content.length > 0) {
          <table>
            <thead>
              <tr>
                <th class="sortable" [class.active]="sort().field === 'nome'" (click)="ordenar('nome')">
                  Nome{{ indicator('nome') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'email'" (click)="ordenar('email')">
                  Email{{ indicator('email') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'cpf'" (click)="ordenar('cpf')">
                  CPF{{ indicator('cpf') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'dataNascimento'" (click)="ordenar('dataNascimento')">
                  Data de Nascimento{{ indicator('dataNascimento') }}
                </th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              @for (aluno of p.content; track aluno.uuid) {
                <tr>
                  <td>{{ aluno.nome }}</td>
                  <td>{{ aluno.email }}</td>
                  <td>{{ aluno.cpf }}</td>
                  <td>{{ aluno.dataNascimento }}</td>
                  <td class="actions">
                    <a [routerLink]="['/matriculas']" [queryParams]="{ alunoUuid: aluno.uuid }" class="btn btn-outline btn-sm">Matriculas</a>
                    <a [routerLink]="['/alunos/editar', aluno.uuid]" class="btn btn-outline btn-sm">Editar</a>
                    <button class="btn btn-danger btn-sm" (click)="deletar(aluno.uuid)">Excluir</button>
                  </td>
                </tr>
              }
            </tbody>
          </table>

          <div class="pagination">
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() === 0" (click)="irParaPagina(currentPage() - 1)">Anterior</button>
            <span>Pagina {{ currentPage() + 1 }} de {{ p.totalPages }} ({{ p.totalElements }} registros)</span>
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() >= p.totalPages - 1" (click)="irParaPagina(currentPage() + 1)">Proxima</button>
          </div>
        } @else {
          <div class="empty-state">
            <p>Nenhum aluno cadastrado.</p>
          </div>
        }
      } @else {
        <div class="empty-state">
          <p>Carregando...</p>
        </div>
      }
    </div>
  `
})
export class AlunoListComponent implements OnInit {
  page = signal<PageResponse<Aluno> | null>(null);
  currentPage = signal(0);
  sort = signal<SortState>({ field: 'nome', direction: 'asc' });

  constructor(
    private alunoService: AlunoService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.alunoService.listar(this.currentPage(), 10, toSortParam(this.sort())).subscribe({
      next: (data) => this.page.set(data),
      error: (err) => handleApiError(err, this.notification)
    });
  }

  ordenar(field: string): void {
    this.sort.set(toggleSort(this.sort(), field));
    this.currentPage.set(0);
    this.carregar();
  }

  indicator(field: string): string {
    return sortIndicator(this.sort(), field);
  }

  irParaPagina(page: number): void {
    this.currentPage.set(page);
    this.carregar();
  }

  deletar(uuid: string): void {
    if (confirm('Tem certeza que deseja excluir este aluno?')) {
      this.alunoService.deletar(uuid).subscribe({
        next: () => {
          this.notification.success('Aluno excluido com sucesso');
          this.carregar();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }
}
