import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { MatriculaService } from '../../../services/matricula.service';
import { AlunoService } from '../../../services/aluno.service';
import { TurmaService } from '../../../services/turma.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { MatriculaRequest } from '../../../models/matricula.model';
import {
  SearchableSelectComponent,
  SearchableSelectFetcher
} from '../../../shared/searchable-select/searchable-select.component';

@Component({
  selector: 'app-matricula-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SearchableSelectComponent],
  template: `
    <div class="page-header">
      <h2>Nova Matrícula</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-group">
          <label for="alunoUuid">Aluno</label>
          <app-searchable-select
            inputId="alunoUuid"
            name="alunoUuid"
            [(ngModel)]="matricula.alunoUuid"
            [fetcher]="fetchAlunos"
            placeholder="Buscar aluno..."
            required
          />
        </div>

        <div class="form-group">
          <label for="turmaUuid">Turma</label>
          <app-searchable-select
            inputId="turmaUuid"
            name="turmaUuid"
            [(ngModel)]="matricula.turmaUuid"
            [fetcher]="fetchTurmas"
            placeholder="Buscar turma..."
            required
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Matricular</button>
          <a routerLink="/matriculas" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class MatriculaFormComponent {
  matricula: MatriculaRequest = { alunoUuid: '', turmaUuid: '' };

  readonly fetchAlunos: SearchableSelectFetcher = ({ page, size, query }) =>
    this.alunoService.listar(page, size, 'nome,asc', query || undefined).pipe(
      map(res => ({
        content: res.content.map(a => ({ value: a.uuid, label: `${a.nome} (${a.email})` })),
        page: res.page,
        totalPages: res.totalPages
      }))
    );

  readonly fetchTurmas: SearchableSelectFetcher = ({ page, size, query }) =>
    this.turmaService.listar(page, size, 'codigo,asc', { q: query || undefined }).pipe(
      map(res => ({
        content: res.content.map(t => {
          const indisponivel = t.status !== 'ABERTA' || t.vagasOcupadas >= t.vagas;
          const sufixo = [
            t.status !== 'ABERTA' ? '[FECHADA]' : '',
            t.vagasOcupadas >= t.vagas ? '[LOTADA]' : ''
          ].filter(Boolean).join(' ');
          return {
            value: t.uuid,
            label: `${t.codigo} - ${t.disciplinaNome} | ${t.professor} | ${t.semestre} (${t.vagasOcupadas}/${t.vagas} vagas)${sufixo ? ' ' + sufixo : ''}`,
            disabled: indisponivel
          };
        }),
        page: res.page,
        totalPages: res.totalPages
      }))
    );

  constructor(
    private matriculaService: MatriculaService,
    private alunoService: AlunoService,
    private turmaService: TurmaService,
    private notification: NotificationService,
    private router: Router
  ) {}

  salvar(): void {
    this.matriculaService.criar(this.matricula).subscribe({
      next: () => {
        this.notification.success('Matrícula realizada com sucesso (status: PENDENTE)');
        this.router.navigate(['/matriculas']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
