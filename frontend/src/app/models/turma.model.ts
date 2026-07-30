export type StatusTurma = 'ABERTA' | 'FECHADA';

export interface Turma {
  uuid: string;
  codigo: string;
  disciplinaUuid: string;
  disciplinaNome: string;
  professor: string;
  semestre: string;
  vagas: number;
  vagasOcupadas: number;
  status: StatusTurma;
  createdAt: string;
  updatedAt: string;
}

export interface TurmaRequest {
  codigo: string;
  disciplinaUuid: string;
  professor: string;
  semestre: string;
  vagas: number;
  status: StatusTurma;
}
