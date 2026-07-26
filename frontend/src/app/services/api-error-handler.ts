import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from './notification.service';

export function handleApiError(error: HttpErrorResponse, notification: NotificationService): void {
  if (error.status === 400 && error.error?.details) {
    const messages = error.error.details
      .map((d: { campo: string; mensagem: string }) => `${d.campo}: ${d.mensagem}`)
      .join('\n');
    notification.error(messages);
  } else if (error.status === 422 || error.status === 409) {
    notification.error(error.error?.message || 'Regra de negocio violada');
  } else if (error.status === 404) {
    notification.error(error.error?.message || 'Recurso nao encontrado');
  } else {
    notification.error('Ocorreu um erro inesperado. Tente novamente mais tarde.');
  }
}
