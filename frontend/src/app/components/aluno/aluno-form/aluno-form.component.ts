import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AlunoService } from '../../../services/aluno.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { AlunoRequest } from '../../../models/aluno.model';

@Component({
  selector: 'app-aluno-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>{{ isEditing ? 'Editar Aluno' : 'Novo Aluno' }}</h2>
    </div>

    <div class="card">
      <form (ngSubmit)="salvar()" #form="ngForm">
        <div class="form-row">
          <div class="form-group">
            <label for="nome">Nome</label>
            <input id="nome" type="text" [(ngModel)]="aluno.nome" name="nome" required
                   [class.invalid]="nomeField.invalid && nomeField.touched" #nomeField="ngModel">
            @if (nomeField.invalid && nomeField.touched) {
              <span class="error-msg">Nome e obrigatorio</span>
            }
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" [(ngModel)]="aluno.email" name="email" required email
                   [class.invalid]="emailField.invalid && emailField.touched" #emailField="ngModel">
            @if (emailField.invalid && emailField.touched) {
              <span class="error-msg">Email valido e obrigatorio</span>
            }
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="cpf">CPF</label>
            <input id="cpf" type="text" [(ngModel)]="aluno.cpf" name="cpf" required minlength="11" maxlength="14"
                   [class.invalid]="cpfField.invalid && cpfField.touched" #cpfField="ngModel">
            @if (cpfField.invalid && cpfField.touched) {
              <span class="error-msg">CPF e obrigatorio (11-14 caracteres)</span>
            }
          </div>
          <div class="form-group">
            <label for="dataNascimento">Data de Nascimento</label>
            <input id="dataNascimento" type="date" [(ngModel)]="aluno.dataNascimento" name="dataNascimento" required
                   [class.invalid]="dataField.invalid && dataField.touched" #dataField="ngModel">
            @if (dataField.invalid && dataField.touched) {
              <span class="error-msg">Data de nascimento e obrigatoria</span>
            }
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid">Salvar</button>
          <a routerLink="/alunos" class="btn btn-outline">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class AlunoFormComponent implements OnInit {
  aluno: AlunoRequest = { nome: '', email: '', cpf: '', dataNascimento: '' };
  isEditing = false;
  private uuid: string | null = null;

  constructor(
    private alunoService: AlunoService,
    private notification: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get('uuid');
    if (this.uuid) {
      this.isEditing = true;
      this.alunoService.buscarPorUuid(this.uuid).subscribe({
        next: (data) => {
          this.aluno = {
            nome: data.nome,
            email: data.email,
            cpf: data.cpf,
            dataNascimento: data.dataNascimento
          };
          this.cdr.markForCheck();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }

  salvar(): void {
    const obs = this.isEditing && this.uuid
      ? this.alunoService.atualizar(this.uuid, this.aluno)
      : this.alunoService.criar(this.aluno);

    obs.subscribe({
      next: () => {
        this.notification.success(this.isEditing ? 'Aluno atualizado com sucesso' : 'Aluno cadastrado com sucesso');
        this.router.navigate(['/alunos']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
