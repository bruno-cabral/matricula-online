import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'alunos', pathMatch: 'full' },
  {
    path: 'alunos',
    loadComponent: () => import('./components/aluno/aluno-list/aluno-list.component').then(m => m.AlunoListComponent)
  },
  {
    path: 'alunos/novo',
    loadComponent: () => import('./components/aluno/aluno-form/aluno-form.component').then(m => m.AlunoFormComponent)
  },
  {
    path: 'alunos/editar/:uuid',
    loadComponent: () => import('./components/aluno/aluno-form/aluno-form.component').then(m => m.AlunoFormComponent)
  },
  {
    path: 'cursos',
    loadComponent: () => import('./components/curso/curso-list/curso-list.component').then(m => m.CursoListComponent)
  },
  {
    path: 'cursos/novo',
    loadComponent: () => import('./components/curso/curso-form/curso-form.component').then(m => m.CursoFormComponent)
  },
  {
    path: 'cursos/editar/:uuid',
    loadComponent: () => import('./components/curso/curso-form/curso-form.component').then(m => m.CursoFormComponent)
  },
  {
    path: 'disciplinas',
    loadComponent: () => import('./components/disciplina/disciplina-list/disciplina-list.component').then(m => m.DisciplinaListComponent)
  },
  {
    path: 'disciplinas/novo',
    loadComponent: () => import('./components/disciplina/disciplina-form/disciplina-form.component').then(m => m.DisciplinaFormComponent)
  },
  {
    path: 'disciplinas/editar/:uuid',
    loadComponent: () => import('./components/disciplina/disciplina-form/disciplina-form.component').then(m => m.DisciplinaFormComponent)
  },
  {
    path: 'turmas',
    loadComponent: () => import('./components/turma/turma-list/turma-list.component').then(m => m.TurmaListComponent)
  },
  {
    path: 'turmas/novo',
    loadComponent: () => import('./components/turma/turma-form/turma-form.component').then(m => m.TurmaFormComponent)
  },
  {
    path: 'turmas/editar/:uuid',
    loadComponent: () => import('./components/turma/turma-form/turma-form.component').then(m => m.TurmaFormComponent)
  },
  {
    path: 'matriculas',
    loadComponent: () => import('./components/matricula/matricula-list/matricula-list.component').then(m => m.MatriculaListComponent)
  },
  {
    path: 'matriculas/nova',
    loadComponent: () => import('./components/matricula/matricula-form/matricula-form.component').then(m => m.MatriculaFormComponent)
  }
];
