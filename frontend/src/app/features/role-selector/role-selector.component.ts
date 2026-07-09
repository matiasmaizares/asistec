import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-selector',
  standalone: true,
  template: `
    <div class="role-page">
      <div class="role-card">
        <div class="logo">🏫</div>
        <h1>Asistec</h1>
        <p>Sistema de Asistencia Escolar — Innova Schools</p>
        <h2>¿Cómo querés ingresar?</h2>
        <div class="role-buttons">
          <button class="btn btn-professor" (click)="enter('professor')">
            <span class="icon">👨‍🏫</span>
            <span class="label">Profesor</span>
            <span class="desc">Registrar asistencia</span>
          </button>
          <button class="btn btn-coordinator" (click)="enter('coordinator')">
            <span class="icon">📊</span>
            <span class="label">Coordinador</span>
            <span class="desc">Ver reportes</span>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .role-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
    }
    .role-card {
      background: white;
      border-radius: 16px;
      padding: 48px;
      text-align: center;
      max-width: 480px;
      width: 90%;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
    }
    .logo { font-size: 56px; margin-bottom: 8px; }
    h1 { font-size: 32px; color: #1a237e; margin: 0 0 8px; }
    p { color: #666; margin: 0 0 32px; font-size: 14px; }
    h2 { color: #333; font-size: 18px; margin-bottom: 24px; }
    .role-buttons { display: flex; gap: 16px; justify-content: center; }
    .btn {
      display: flex; flex-direction: column; align-items: center;
      padding: 24px 32px; border: 2px solid transparent;
      border-radius: 12px; cursor: pointer; transition: all 0.2s;
      background: #f5f5f5; flex: 1;
    }
    .btn:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.15); }
    .btn-professor:hover { border-color: #1976d2; background: #e3f2fd; }
    .btn-coordinator:hover { border-color: #388e3c; background: #e8f5e9; }
    .icon { font-size: 36px; margin-bottom: 8px; }
    .label { font-size: 18px; font-weight: 700; color: #333; }
    .desc { font-size: 12px; color: #888; margin-top: 4px; }
  `]
})
export class RoleSelectorComponent {
  private router = inject(Router);

  enter(role: 'professor' | 'coordinator') {
    this.router.navigate([`/${role}`]);
  }
}
