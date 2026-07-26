export interface Disciplina {
  uuid: string;
  nome: string;
  descricao: string;
  cargaHoraria: number;
  cursoUuid: string;
  cursoNome: string;
  createdAt: string;
  updatedAt: string;
}

export interface DisciplinaRequest {
  nome: string;
  descricao: string;
  cargaHoraria: number;
  cursoUuid: string;
}
