import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TurmaService } from '../../../services/turma.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { Turma } from '../../../models/turma.model';
import { PageResponse } from '../../../models/page.model';
import { SortState, sortIndicator, toggleSort, toSortParam } from '../../../shared/sort.util';

@Component({
  selector: 'app-turma-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Turmas</h2>
      <a routerLink="/turmas/novo" class="btn btn-primary">+ Nova Turma</a>
    </div>

    <div class="card">
      @if (page(); as p) {
        @if (p.content.length > 0) {
          <table>
            <thead>
              <tr>
                <th class="sortable" [class.active]="sort().field === 'codigo'" (click)="ordenar('codigo')">
                  Código{{ indicator('codigo') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'disciplina.nome'" (click)="ordenar('disciplina.nome')">
                  Disciplina{{ indicator('disciplina.nome') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'professor'" (click)="ordenar('professor')">
                  Professor{{ indicator('professor') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'semestre'" (click)="ordenar('semestre')">
                  Semestre{{ indicator('semestre') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'vagas'" (click)="ordenar('vagas')">
                  Vagas{{ indicator('vagas') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'status'" (click)="ordenar('status')">
                  Status{{ indicator('status') }}
                </th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              @for (t of p.content; track t.uuid) {
                <tr>
                  <td>{{ t.codigo }}</td>
                  <td>{{ t.disciplinaNome }}</td>
                  <td>{{ t.professor }}</td>
                  <td>{{ t.semestre }}</td>
                  <td>{{ t.vagasOcupadas }}/{{ t.vagas }}</td>
                  <td>
                    <span class="badge" [class.badge-aberta]="t.status === 'ABERTA'" [class.badge-fechada]="t.status === 'FECHADA'">
                      {{ t.status }}
                    </span>
                  </td>
                  <td class="actions">
                    <a [routerLink]="['/matriculas']" [queryParams]="{ turmaUuid: t.uuid }" class="btn btn-outline btn-sm">Matrículas</a>
                    <a [routerLink]="['/turmas/editar', t.uuid]" class="btn btn-outline btn-sm">Editar</a>
                    <button class="btn btn-danger btn-sm" (click)="deletar(t.uuid)">Excluir</button>
                  </td>
                </tr>
              }
            </tbody>
          </table>

          <div class="pagination">
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() === 0" (click)="irParaPagina(currentPage() - 1)">Anterior</button>
            <span>Página {{ currentPage() + 1 }} de {{ p.totalPages }}</span>
            <button class="btn btn-outline btn-sm" [disabled]="currentPage() >= p.totalPages - 1" (click)="irParaPagina(currentPage() + 1)">Próxima</button>
          </div>
        } @else {
          <div class="empty-state"><p>Nenhuma turma cadastrada.</p></div>
        }
      } @else {
        <div class="empty-state"><p>Carregando...</p></div>
      }
    </div>
  `
})
export class TurmaListComponent implements OnInit {
  page = signal<PageResponse<Turma> | null>(null);
  currentPage = signal(0);
  sort = signal<SortState>({ field: 'codigo', direction: 'asc' });

  constructor(private turmaService: TurmaService, private notification: NotificationService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.turmaService.listar(this.currentPage(), 10, toSortParam(this.sort())).subscribe({
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
    if (confirm('Excluir turma?')) {
      this.turmaService.deletar(uuid).subscribe({
        next: () => { this.notification.success('Turma excluída'); this.carregar(); },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }
}
