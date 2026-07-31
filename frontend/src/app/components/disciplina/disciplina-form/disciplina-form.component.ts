import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { DisciplinaService } from '../../../services/disciplina.service';
import { CursoService } from '../../../services/curso.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { DisciplinaRequest } from '../../../models/disciplina.model';
import {
  SearchableSelectComponent,
  SearchableSelectFetcher
} from '../../../shared/searchable-select/searchable-select.component';

@Component({
  selector: 'app-disciplina-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SearchableSelectComponent],
  template: `
    <div class="page-header">
      <h2>{{ isEditing ? 'Editar Disciplina' : 'Nova Disciplina' }}</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-group">
          <label for="nome">Nome</label>
          <input id="nome" type="text" [(ngModel)]="disciplina.nome" name="nome" required>
        </div>

        <div class="form-group">
          <label for="descricao">Descrição</label>
          <textarea id="descricao" [(ngModel)]="disciplina.descricao" name="descricao" rows="3"></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="cargaHoraria">Carga Horária (horas)</label>
            <input id="cargaHoraria" type="number" [(ngModel)]="disciplina.cargaHoraria" name="cargaHoraria" required min="1">
          </div>
          <div class="form-group">
            <label for="cursoUuid">Curso</label>
            <app-searchable-select
              inputId="cursoUuid"
              name="cursoUuid"
              [(ngModel)]="disciplina.cursoUuid"
              [fetcher]="fetchCursos"
              [selectedLabel]="cursoLabel()"
              placeholder="Buscar curso..."
              required
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Salvar</button>
          <a routerLink="/disciplinas" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class DisciplinaFormComponent implements OnInit {
  disciplina: DisciplinaRequest = { nome: '', descricao: '', cargaHoraria: 0, cursoUuid: '' };
  cursoLabel = signal('');
  isEditing = false;
  private uuid: string | null = null;

  readonly fetchCursos: SearchableSelectFetcher = ({ page, size, query }) =>
    this.cursoService.listar(page, size, 'nome,asc', query || undefined).pipe(
      map(res => ({
        content: res.content.map(c => ({ value: c.uuid, label: c.nome })),
        page: res.page,
        totalPages: res.totalPages
      }))
    );

  constructor(
    private disciplinaService: DisciplinaService,
    private cursoService: CursoService,
    private notification: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get('uuid');
    if (this.uuid) {
      this.isEditing = true;
      this.disciplinaService.buscarPorUuid(this.uuid).subscribe({
        next: (data) => {
          this.disciplina = {
            nome: data.nome,
            descricao: data.descricao,
            cargaHoraria: data.cargaHoraria,
            cursoUuid: data.cursoUuid
          };
          this.cursoLabel.set(data.cursoNome);
          this.cdr.markForCheck();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }

  salvar(): void {
    const obs = this.isEditing && this.uuid
      ? this.disciplinaService.atualizar(this.uuid, this.disciplina)
      : this.disciplinaService.criar(this.disciplina);

    obs.subscribe({
      next: () => {
        this.notification.success(this.isEditing ? 'Disciplina atualizada' : 'Disciplina cadastrada');
        this.router.navigate(['/disciplinas']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
