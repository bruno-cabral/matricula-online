export interface Aluno {
  uuid: string;
  nome: string;
  email: string;
  cpf: string;
  dataNascimento: string;
  createdAt: string;
  updatedAt: string;
}

export interface AlunoRequest {
  nome: string;
  email: string;
  cpf: string;
  dataNascimento: string;
}
