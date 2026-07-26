export interface Curso {
  uuid: string;
  nome: string;
  descricao: string;
  cargaHoraria: number;
  createdAt: string;
  updatedAt: string;
}

export interface CursoRequest {
  nome: string;
  descricao: string;
  cargaHoraria: number;
}
