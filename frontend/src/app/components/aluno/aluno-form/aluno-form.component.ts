import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AlunoService } from '../../../services/aluno.service';
import { NotificationService } from '../../../services/notification.service';
import { handleApiError } from '../../../services/api-error-handler';
import { AlunoRequest } from '../../../models/aluno.model';
import { CpfValidatorDirective } from '../../../directives/cpf-validator.directive';
import { CpfMaskDirective } from '../../../directives/cpf-mask.directive';
import { DateInputComponent } from '../../../shared/date-input/date-input.component';
import { apenasDigitosCpf, formatCpf } from '../../../shared/cpf.util';
import { normalizeToIsoDate } from '../../../shared/date.util';

@Component({
  selector: 'app-aluno-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    CpfValidatorDirective,
    CpfMaskDirective,
    DateInputComponent
  ],
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
              <span class="error-msg">Nome é obrigatório</span>
            }
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" [(ngModel)]="aluno.email" name="email" required email
                   [class.invalid]="emailField.invalid && emailField.touched" #emailField="ngModel">
            @if (emailField.invalid && emailField.touched) {
              <span class="error-msg">Email válido é obrigatório</span>
            }
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="cpf">CPF</label>
            <input id="cpf" type="text" [(ngModel)]="aluno.cpf" name="cpf" required cpfValidator cpfMask
                   maxlength="14" inputmode="numeric" placeholder="000.000.000-00"
                   [class.invalid]="cpfField.invalid && cpfField.touched" #cpfField="ngModel">
            @if (cpfField.errors?.['required'] && cpfField.touched) {
              <span class="error-msg">CPF é obrigatório</span>
            } @else if (cpfField.errors?.['cpf'] && cpfField.touched) {
              <span class="error-msg">CPF inválido</span>
            }
          </div>
          <div class="form-group">
            <label for="dataNascimento">Data de Nascimento</label>
            <app-date-input
              inputId="dataNascimento"
              name="dataNascimento"
              [(ngModel)]="aluno.dataNascimento"
              required
              [class.invalid]="dataField.invalid && dataField.touched"
              #dataField="ngModel"
            />
            @if (dataField.invalid && dataField.touched) {
              <span class="error-msg">Data de nascimento é obrigatória</span>
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
            cpf: formatCpf(data.cpf),
            dataNascimento: normalizeToIsoDate(data.dataNascimento)
          };
          this.cdr.markForCheck();
        },
        error: (err) => handleApiError(err, this.notification)
      });
    }
  }

  salvar(): void {
    const payload: AlunoRequest = {
      ...this.aluno,
      cpf: apenasDigitosCpf(this.aluno.cpf)
    };

    const obs = this.isEditing && this.uuid
      ? this.alunoService.atualizar(this.uuid, payload)
      : this.alunoService.criar(payload);

    obs.subscribe({
      next: () => {
        this.notification.success(this.isEditing ? 'Aluno atualizado com sucesso' : 'Aluno cadastrado com sucesso');
        this.router.navigate(['/alunos']);
      },
      error: (err) => handleApiError(err, this.notification)
    });
  }
}
