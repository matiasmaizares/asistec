import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-page">
      <div class="login-card">
        <div class="logo">🏫</div>
        <h1>Asistec</h1>
        <p>Sistema de Asistencia Escolar — Innova Schools</p>

        <form (ngSubmit)="submit()">
          <label>
            Email
            <input type="email" name="email" [(ngModel)]="email" required autocomplete="username" />
          </label>
          <label>
            Contraseña
            <input type="password" name="password" [(ngModel)]="password" required autocomplete="current-password" />
          </label>

          @if (errorMsg) {
            <div class="alert-error">❌ {{ errorMsg }}</div>
          }

          <button type="submit" [disabled]="loading">
            {{ loading ? 'Ingresando...' : 'Ingresar' }}
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
    }
    .login-card {
      background: white;
      border-radius: 16px;
      padding: 48px;
      text-align: center;
      max-width: 400px;
      width: 90%;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
    }
    .logo { font-size: 56px; margin-bottom: 8px; }
    h1 { font-size: 32px; color: #1a237e; margin: 0 0 8px; }
    p { color: #666; margin: 0 0 32px; font-size: 14px; }
    form { display: flex; flex-direction: column; gap: 16px; text-align: left; }
    label { display: flex; flex-direction: column; gap: 6px; font-size: 13px; font-weight: 600; color: #333; }
    input {
      padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px;
      font-size: 14px; font-family: inherit;
    }
    input:focus { outline: none; border-color: #1976d2; }
    button {
      margin-top: 8px; padding: 12px; border: none; border-radius: 8px;
      background: #1a237e; color: white; font-size: 15px; font-weight: 700;
      cursor: pointer; transition: background 0.2s;
    }
    button:hover:not(:disabled) { background: #283593; }
    button:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error {
      background: #ffebee; color: #c62828; padding: 10px 12px;
      border-radius: 8px; font-size: 13px;
    }
  `]
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  loading = false;
  errorMsg = '';

  submit() {
    this.loading = true;
    this.errorMsg = '';
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigate([res.teacher.role === 'COORDINADOR' ? '/coordinator' : '/professor']);
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Email o contraseña incorrectos';
      },
    });
  }
}
