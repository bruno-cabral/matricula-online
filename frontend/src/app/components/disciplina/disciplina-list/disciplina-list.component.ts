import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DisciplinaService } from '../../../services/disciplina.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { Disciplina } from '../../../models/disciplina.model';
import { PageResponse } from '../../../models/page.model';
import { SortState, sortIndicator, toggleSort, toSortParam } from '../../../shared/sort.util';

@Component({
  selector: 'app-disciplina-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Disciplinas</h2>
      <a routerLink="/disciplinas/novo" class="btn btn-primary">+ Nova Disciplina</a>
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
                <th class="sortable" [class.active]="sort().field === 'curso.nome'" (click)="ordenar('curso.nome')">
                  Curso{{ indicator('curso.nome') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'cargaHoraria'" (click)="ordenar('cargaHoraria')">
                  Carga Horaria{{ indicator('cargaHoraria') }}
                </th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              @for (d of p.content; track d.uuid) {
                <tr>
                  <td>{{ d.nome }}</td>
                  <td>{{ d.cursoNome }}</td>
                  <td>{{ d.cargaHoraria }}h</td>
                  <td class="actions">
                    <a [routerLink]="['/disciplinas/editar', d.uuid]" class="btn btn-outline btn-sm">Editar</a>
                    <button class="btn btn-danger btn-sm" (click)="deletar(d.uuid)">Excluir</button>
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
          <div class="empty-state"><p>Nenhuma disciplina cadastrada.</p></div>
        }
      } @else {
        <div class="empty-state"><p>Carregando...</p></div>
      }
    </div>
  `
})
export class DisciplinaListComponent implements OnInit {
  page = signal<PageResponse<Disciplina> | null>(null);
  currentPage = signal(0);
  sort = signal<SortState>({ field: 'nome', direction: 'asc' });

  constructor(private disciplinaService: DisciplinaService, private notification: NotificationService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.disciplinaService.listar(this.currentPage(), 10, toSortParam(this.sort())).subscribe({
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
    if (confirm('Excluir disciplina?')) {
      this.disciplinaService.deletar(uuid).subscribe({
        next: () => { this.notification.success('Disciplina excluida'); this.carregar(); },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }
}
