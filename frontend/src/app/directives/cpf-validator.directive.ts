import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { isCpfValido } from '../shared/cpf.util';

@Directive({
  selector: '[cpfValidator]',
  standalone: true,
  providers: [{ provide: NG_VALIDATORS, useExisting: CpfValidatorDirective, multi: true }]
})
export class CpfValidatorDirective implements Validator {
  validate(control: AbstractControl): ValidationErrors | null {
    const value = control.value as string | null | undefined;
    if (!value) {
      return null;
    }
    return isCpfValido(value) ? null : { cpf: true };
  }
}
