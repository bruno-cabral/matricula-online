export interface Matricula {
  uuid: string;
  alunoUuid: string;
  alunoNome: string;
  turmaUuid: string;
  turmaCodigo: string;
  status: 'PENDENTE' | 'CONFIRMADA' | 'CANCELADA';
  dataMatricula: string;
  createdAt: string;
  updatedAt: string;
}

export interface MatriculaRequest {
  alunoUuid: string;
  turmaUuid: string;
}
