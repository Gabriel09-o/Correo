import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EmailService } from './email.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  private fb = inject(FormBuilder);
  private emailService = inject(EmailService);

  form = this.fb.nonNullable.group({
    to: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    subject: ['', [Validators.required, Validators.maxLength(150)]],
    message: ['', [Validators.required, Validators.maxLength(5000)]],
  });

  loading = false;
  resultado: { tipo: 'ok' | 'error'; texto: string } | null = null;

  enviar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.loading) return; // evita envíos dobles

    this.loading = true;
    this.resultado = null;

    this.emailService.send(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading = false;
        this.resultado = {
          tipo: 'ok',
          texto: 'Solicitud de envío aceptada. Revisa la bandeja (y la carpeta de spam) del destinatario.',
        };
        this.form.reset();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.resultado = {
          tipo: 'error',
          texto: err.error?.message ?? 'No se pudo conectar con el servidor. Intenta de nuevo.',
        };
      },
    });
  }
}
