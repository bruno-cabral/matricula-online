import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CursoService } from '../../../services/curso.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { Curso } from '../../../models/curso.model';
import { PageResponse } from '../../../models/page.model';
import { SortState, sortIndicator, toggleSort, toSortParam } from '../../../shared/sort.util';

@Component({
  selector: 'app-curso-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Cursos</h2>
      <a routerLink="/cursos/novo" class="btn btn-primary">+ Novo Curso</a>
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
                <th class="sortable" [class.active]="sort().field === 'descricao'" (click)="ordenar('descricao')">
                  Descricao{{ indicator('descricao') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'cargaHoraria'" (click)="ordenar('cargaHoraria')">
                  Carga Horaria{{ indicator('cargaHoraria') }}
                </th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              @for (curso of p.content; track curso.uuid) {
                <tr>
                  <td>{{ curso.nome }}</td>
                  <td>{{ curso.descricao }}</td>
                  <td>{{ curso.cargaHoraria }}h</td>
                  <td class="actions">
                    <a [routerLink]="['/cursos/editar', curso.uuid]" class="btn btn-outline btn-sm">Editar</a>
                    <button class="btn btn-danger btn-sm" (click)="deletar(curso.uuid)">Excluir</button>
                  </td>
                </tr>
              }
            </tbody>
          </table>

          <div class="pagination">
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() === 0" (click)="irParaPagina(currentPage() - 1)">Anterior</button>
            <span>Pagina {{ currentPage() + 1 }} de {{ p.totalPages }}</span>
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() >= p.totalPages - 1" (click)="irParaPagina(currentPage() + 1)">Proxima</button>
          </div>
        } @else {
          <div class="empty-state"><p>Nenhum curso cadastrado.</p></div>
        }
      } @else {
        <div class="empty-state"><p>Carregando...</p></div>
      }
    </div>
  `
})
export class CursoListComponent implements OnInit {
  page = signal<PageResponse<Curso> | null>(null);
  currentPage = signal(0);
  sort = signal<SortState>({ field: 'nome', direction: 'asc' });

  constructor(private cursoService: CursoService, private notification: NotificationService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.cursoService.listar(this.currentPage(), 10, toSortParam(this.sort())).subscribe({
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

  irParaPagina(page: number): void { this.currentPage.set(page); this.carregar(); }

  deletar(uuid: string): void {
    if (confirm('Tem certeza que deseja excluir este curso?')) {
      this.cursoService.deletar(uuid).subscribe({
        next: () => { this.notification.success('Curso excluido com sucesso'); this.carregar(); },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }
}
