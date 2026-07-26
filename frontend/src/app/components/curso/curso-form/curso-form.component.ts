import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CursoService } from '../../../services/curso.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { CursoRequest } from '../../../models/curso.model';

@Component({
  selector: 'app-curso-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>{{ isEditing ? 'Editar Curso' : 'Novo Curso' }}</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-group">
          <label for="nome">Nome</label>
          <input id="nome" type="text" [(ngModel)]="curso.nome" name="nome" required
                 [class.invalid]="nomeField.invalid && nomeField.touched" #nomeField="ngModel">
          @if (nomeField.invalid && nomeField.touched) {
            <span class="error-msg">Nome e obrigatorio</span>
          }
        </div>

        <div class="form-group">
          <label for="descricao">Descricao</label>
          <textarea id="descricao" [(ngModel)]="curso.descricao" name="descricao" rows="3"></textarea>
        </div>

        <div class="form-group">
          <label for="cargaHoraria">Carga Horaria (horas)</label>
          <input id="cargaHoraria" type="number" [(ngModel)]="curso.cargaHoraria" name="cargaHoraria" required min="1"
                 [class.invalid]="chField.invalid && chField.touched" #chField="ngModel">
          @if (chField.invalid && chField.touched) {
            <span class="error-msg">Carga horaria e obrigatoria e deve ser maior que zero</span>
          }
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Salvar</button>
          <a routerLink="/cursos" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class CursoFormComponent implements OnInit {
  curso: CursoRequest = { nome: '', descricao: '', cargaHoraria: 0 };
  isEditing = false;
  private uuid: string | null = null;

  constructor(
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
      this.cursoService.buscarPorUuid(this.uuid).subscribe({
        next: (data) => {
          this.curso = { nome: data.nome, descricao: data.descricao, cargaHoraria: data.cargaHoraria };
          this.cdr.markForCheck();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }

  salvar(): void {
    const obs = this.isEditing && this.uuid
      ? this.cursoService.atualizar(this.uuid, this.curso)
      : this.cursoService.criar(this.curso);

    obs.subscribe({
      next: () => {
        this.notification.success(this.isEditing ? 'Curso atualizado' : 'Curso cadastrado');
        this.router.navigate(['/cursos']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
