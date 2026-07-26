import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatriculaService } from '../../../services/matricula.service';
import { AlunoService } from '../../../services/aluno.service';
import { TurmaService } from '../../../services/turma.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { Matricula } from '../../../models/matricula.model';
import { Aluno } from '../../../models/aluno.model';
import { Turma } from '../../../models/turma.model';
import { PageResponse } from '../../../models/page.model';
import { SortState, sortIndicator, toggleSort, toSortParam } from '../../../shared/sort.util';

type ConsultaTipo = 'todas' | 'aluno' | 'turma';

@Component({
  selector: 'app-matricula-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Matriculas</h2>
      <a routerLink="/matriculas/nova" class="btn btn-primary">+ Nova Matricula</a>
    </div>

    <div class="card">
      <div class="filter-bar filter-bar-wrap">
        <div class="filter-group">
          <label for="consultaTipo">Consultar:</label>
          <select id="consultaTipo" [ngModel]="consultaTipo()" (ngModelChange)="alterarConsulta($event)">
            <option value="todas">Todas as matriculas</option>
            <option value="aluno">Por aluno</option>
            <option value="turma">Por turma</option>
          </select>
        </div>

        @if (consultaTipo() === 'aluno') {
          <div class="filter-group">
            <label for="alunoUuid">Aluno:</label>
            <select id="alunoUuid" [ngModel]="alunoUuid()" (ngModelChange)="alterarAluno($event)">
              <option value="">Selecione um aluno</option>
              @for (a of alunos(); track a.uuid) {
                <option [value]="a.uuid">{{ a.nome }} ({{ a.email }})</option>
              }
            </select>
          </div>
        }

        @if (consultaTipo() === 'turma') {
          <div class="filter-group">
            <label for="turmaUuid">Turma:</label>
            <select id="turmaUuid" [ngModel]="turmaUuid()" (ngModelChange)="alterarTurma($event)">
              <option value="">Selecione uma turma</option>
              @for (t of turmas(); track t.uuid) {
                <option [value]="t.uuid">{{ t.codigo }} — {{ t.disciplinaNome }}</option>
              }
            </select>
          </div>
        }

        <div class="filter-group">
          <label for="statusFilter">Status:</label>
          <select id="statusFilter" [ngModel]="statusFilter()" (ngModelChange)="filtrarStatus($event)">
            <option value="">Todos</option>
            <option value="PENDENTE">Pendente</option>
            <option value="CONFIRMADA">Confirmada</option>
            <option value="CANCELADA">Cancelada</option>
          </select>
        </div>
      </div>

      @if (consultaTipo() === 'aluno' && !alunoUuid()) {
        <div class="empty-state"><p>Selecione um aluno para consultar as matriculas.</p></div>
      } @else if (consultaTipo() === 'turma' && !turmaUuid()) {
        <div class="empty-state"><p>Selecione uma turma para consultar as matriculas.</p></div>
      } @else if (page(); as p) {
        @if (p.content.length > 0) {
          <table>
            <thead>
              <tr>
                <th class="sortable" [class.active]="sort().field === 'aluno.nome'" (click)="ordenar('aluno.nome')">
                  Aluno{{ indicator('aluno.nome') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'turma.codigo'" (click)="ordenar('turma.codigo')">
                  Turma{{ indicator('turma.codigo') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'status'" (click)="ordenar('status')">
                  Status{{ indicator('status') }}
                </th>
                <th class="sortable" [class.active]="sort().field === 'dataMatricula'" (click)="ordenar('dataMatricula')">
                  Data Matricula{{ indicator('dataMatricula') }}
                </th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              @for (m of p.content; track m.uuid) {
                <tr>
                  <td>{{ m.alunoNome }}</td>
                  <td>{{ m.turmaCodigo }}</td>
                  <td>
                    <span class="badge"
                      [class.badge-pendente]="m.status === 'PENDENTE'"
                      [class.badge-confirmada]="m.status === 'CONFIRMADA'"
                      [class.badge-cancelada]="m.status === 'CANCELADA'">
                      {{ m.status }}
                    </span>
                  </td>
                  <td>{{ m.dataMatricula | date:'dd/MM/yyyy HH:mm' }}</td>
                  <td class="actions">
                    @if (m.status === 'PENDENTE') {
                      <button class="btn btn-success btn-sm" (click)="confirmar(m.uuid)">Confirmar</button>
                      <button class="btn btn-danger btn-sm" (click)="cancelar(m.uuid)">Cancelar</button>
                    } @else if (m.status === 'CONFIRMADA') {
                      <button class="btn btn-danger btn-sm" (click)="cancelar(m.uuid)">Cancelar</button>
                    } @else {
                      <span class="actions-empty">—</span>
                    }
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
          <div class="empty-state"><p>Nenhuma matricula encontrada para esta consulta.</p></div>
        }
      } @else {
        <div class="empty-state"><p>Carregando...</p></div>
      }
    </div>
  `
})
export class MatriculaListComponent implements OnInit {
  page = signal<PageResponse<Matricula> | null>(null);
  currentPage = signal(0);
  statusFilter = signal('');
  consultaTipo = signal<ConsultaTipo>('todas');
  alunoUuid = signal('');
  turmaUuid = signal('');
  alunos = signal<Aluno[]>([]);
  turmas = signal<Turma[]>([]);
  sort = signal<SortState>({ field: 'dataMatricula', direction: 'desc' });

  constructor(
    private matriculaService: MatriculaService,
    private alunoService: AlunoService,
    private turmaService: TurmaService,
    private notification: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.alunoService.listar(0, 100, 'nome,asc').subscribe({
      next: (data) => this.alunos.set(data.content),
      error: (err) => handleApiError(err, this.notification)
    });

    this.turmaService.listar(0, 100, 'codigo,asc').subscribe({
      next: (data) => this.turmas.set(data.content),
      error: (err) => handleApiError(err, this.notification)
    });

    const params = this.route.snapshot.queryParamMap;
    const aluno = params.get('alunoUuid');
    const turma = params.get('turmaUuid');

    if (aluno) {
      this.consultaTipo.set('aluno');
      this.alunoUuid.set(aluno);
    } else if (turma) {
      this.consultaTipo.set('turma');
      this.turmaUuid.set(turma);
    }

    this.carregar();
  }

  carregar(): void {
    const status = this.statusFilter() || undefined;
    const page = this.currentPage();
    const tipo = this.consultaTipo();
    const sort = toSortParam(this.sort());

    if (tipo === 'aluno') {
      if (!this.alunoUuid()) {
        this.page.set(null);
        return;
      }
      this.matriculaService.listarPorAluno(this.alunoUuid(), page, 10, status, sort).subscribe({
        next: (data) => this.page.set(data),
        error: (err) => handleApiError(err, this.notification)
      });
      return;
    }

    if (tipo === 'turma') {
      if (!this.turmaUuid()) {
        this.page.set(null);
        return;
      }
      this.matriculaService.listarPorTurma(this.turmaUuid(), page, 10, status, sort).subscribe({
        next: (data) => this.page.set(data),
        error: (err) => handleApiError(err, this.notification)
      });
      return;
    }

    this.matriculaService.listar(page, 10, status, sort).subscribe({
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

  alterarConsulta(tipo: ConsultaTipo): void {
    this.consultaTipo.set(tipo);
    this.currentPage.set(0);
    this.page.set(null);

    if (tipo === 'todas') {
      this.alunoUuid.set('');
      this.turmaUuid.set('');
      this.atualizarQueryParams();
      this.carregar();
    } else if (tipo === 'aluno') {
      this.turmaUuid.set('');
      this.atualizarQueryParams();
      if (this.alunoUuid()) {
        this.carregar();
      }
    } else {
      this.alunoUuid.set('');
      this.atualizarQueryParams();
      if (this.turmaUuid()) {
        this.carregar();
      }
    }
  }

  alterarAluno(uuid: string): void {
    this.alunoUuid.set(uuid);
    this.currentPage.set(0);
    this.atualizarQueryParams();
    this.carregar();
  }

  alterarTurma(uuid: string): void {
    this.turmaUuid.set(uuid);
    this.currentPage.set(0);
    this.atualizarQueryParams();
    this.carregar();
  }

  filtrarStatus(status: string): void {
    this.statusFilter.set(status);
    this.currentPage.set(0);
    this.carregar();
  }

  irParaPagina(page: number): void {
    this.currentPage.set(page);
    this.carregar();
  }

  private atualizarQueryParams(): void {
    const queryParams: Record<string, string | null> = {
      alunoUuid: this.consultaTipo() === 'aluno' && this.alunoUuid() ? this.alunoUuid() : null,
      turmaUuid: this.consultaTipo() === 'turma' && this.turmaUuid() ? this.turmaUuid() : null
    };
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
  }

  confirmar(uuid: string): void {
    this.matriculaService.confirmar(uuid).subscribe({
      next: () => { this.notification.success('Matricula confirmada com sucesso'); this.carregar(); },
      error: (err) => handleApiError(err, this.notification)
    });
  }

  cancelar(uuid: string): void {
    if (confirm('Tem certeza que deseja cancelar esta matricula?')) {
      this.matriculaService.cancelar(uuid).subscribe({
        next: () => { this.notification.success('Matricula cancelada'); this.carregar(); },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }
}
