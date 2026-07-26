import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatriculaService } from '../../../services/matricula.service';
import { AlunoService } from '../../../services/aluno.service';
import { TurmaService } from '../../../services/turma.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { MatriculaRequest } from '../../../models/matricula.model';
import { Aluno } from '../../../models/aluno.model';
import { Turma } from '../../../models/turma.model';

@Component({
  selector: 'app-matricula-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Nova Matricula</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-group">
          <label for="alunoUuid">Aluno</label>
          <select id="alunoUuid" [(ngModel)]="matricula.alunoUuid" name="alunoUuid" required>
            <option value="">Selecione um aluno</option>
            @for (a of alunos(); track a.uuid) {
              <option [value]="a.uuid">{{ a.nome }} ({{ a.email }})</option>
            }
          </select>
        </div>

        <div class="form-group">
          <label for="turmaUuid">Turma</label>
          <select id="turmaUuid" [(ngModel)]="matricula.turmaUuid" name="turmaUuid" required>
            <option value="">Selecione uma turma</option>
            @for (t of turmas(); track t.uuid) {
              <option [value]="t.uuid" [disabled]="t.status !== 'ABERTA' || t.vagasOcupadas >= t.vagas">
                {{ t.codigo }} - {{ t.disciplinaNome }} | {{ t.professor }} | {{ t.semestre }}
                ({{ t.vagasOcupadas }}/{{ t.vagas }} vagas)
                {{ t.status !== 'ABERTA' ? ' [FECHADA]' : '' }}
                {{ t.vagasOcupadas >= t.vagas ? ' [LOTADA]' : '' }}
              </option>
            }
          </select>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Matricular</button>
          <a routerLink="/matriculas" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class MatriculaFormComponent implements OnInit {
  matricula: MatriculaRequest = { alunoUuid: '', turmaUuid: '' };
  alunos = signal<Aluno[]>([]);
  turmas = signal<Turma[]>([]);

  constructor(
    private matriculaService: MatriculaService,
    private alunoService: AlunoService,
    private turmaService: TurmaService,
    private notification: NotificationService,
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
  }

  salvar(): void {
    this.matriculaService.criar(this.matricula).subscribe({
      next: () => {
        this.notification.success('Matricula realizada com sucesso (status: PENDENTE)');
        this.router.navigate(['/matriculas']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
