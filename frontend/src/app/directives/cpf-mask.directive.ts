import { Directive, ElementRef, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';
import { formatCpf } from '../shared/cpf.util';

@Directive({
  selector: '[cpfMask]',
  standalone: true
})
export class CpfMaskDirective {
  private readonly el = inject(ElementRef<HTMLInputElement>);
  private readonly ngControl = inject(NgControl, { optional: true, self: true });

  @HostListener('input')
  onInput(): void {
    const formatted = formatCpf(this.el.nativeElement.value);
    this.el.nativeElement.value = formatted;
    this.ngControl?.control?.setValue(formatted, { emitEvent: true });
  }

  @HostListener('blur')
  onBlur(): void {
    const formatted = formatCpf(this.el.nativeElement.value);
    this.el.nativeElement.value = formatted;
    this.ngControl?.control?.setValue(formatted, { emitEvent: true });
  }
}
