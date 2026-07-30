import { ChangeDetectorRef, Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { TurmaService } from '../../../services/turma.service';
import { DisciplinaService } from '../../../services/disciplina.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { TurmaRequest } from '../../../models/turma.model';
import { Disciplina } from '../../../models/disciplina.model';
import { SearchableSelectComponent } from '../../../shared/searchable-select/searchable-select.component';

@Component({
  selector: 'app-turma-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SearchableSelectComponent],
  template: `
    <div class="page-header">
      <h2>{{ isEditing ? 'Editar Turma' : 'Nova Turma' }}</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-row">
          <div class="form-group">
            <label for="codigo">Código</label>
            <input id="codigo" type="text" [(ngModel)]="turma.codigo" name="codigo" required>
          </div>
          <div class="form-group">
            <label for="disciplinaUuid">Disciplina</label>
            <app-searchable-select
              inputId="disciplinaUuid"
              name="disciplinaUuid"
              [(ngModel)]="turma.disciplinaUuid"
              [options]="disciplinaOptions()"
              placeholder="Buscar disciplina..."
              required
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="professor">Professor</label>
            <input id="professor" type="text" [(ngModel)]="turma.professor" name="professor" required>
          </div>
          <div class="form-group">
            <label for="semestre">Semestre</label>
            <input id="semestre" type="text" [(ngModel)]="turma.semestre" name="semestre" required placeholder="Ex: 2026.1">
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="vagas">Número de Vagas</label>
            <input id="vagas" type="number" [(ngModel)]="turma.vagas" name="vagas" required min="1">
          </div>
          <div class="form-group">
            <label for="status">Status</label>
            <select id="status" [(ngModel)]="turma.status" name="status" required>
              <option value="ABERTA">Aberta</option>
              <option value="FECHADA">Fechada</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Salvar</button>
          <a routerLink="/turmas" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class TurmaFormComponent implements OnInit {
  turma: TurmaRequest = {
    codigo: '',
    disciplinaUuid: '',
    professor: '',
    semestre: '',
    vagas: 1,
    status: 'ABERTA'
  };
  disciplinas = signal<Disciplina[]>([]);
  isEditing = false;
  private uuid: string | null = null;

  disciplinaOptions = computed(() =>
    this.disciplinas().map(d => ({ value: d.uuid, label: `${d.nome} (${d.cursoNome})` }))
  );

  constructor(
    private turmaService: TurmaService,
    private disciplinaService: DisciplinaService,
    private notification: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.disciplinaService.listar(0, 100).subscribe({
      next: (data) => this.disciplinas.set(data.content),
      error: (err) => handleApiError(err, this.notification)
    });

    this.uuid = this.route.snapshot.paramMap.get('uuid');
    if (this.uuid) {
      this.isEditing = true;
      this.turmaService.buscarPorUuid(this.uuid).subscribe({
        next: (data) => {
          this.turma = {
            codigo: data.codigo,
            disciplinaUuid: data.disciplinaUuid,
            professor: data.professor,
            semestre: data.semestre,
            vagas: data.vagas,
            status: data.status
          };
          this.cdr.markForCheck();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }

  salvar(): void {
    const obs = this.isEditing && this.uuid
      ? this.turmaService.atualizar(this.uuid, this.turma)
      : this.turmaService.criar(this.turma);

    obs.subscribe({
      next: () => {
        this.notification.success(this.isEditing ? 'Turma atualizada' : 'Turma cadastrada');
        this.router.navigate(['/turmas']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
