import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SectionService } from '../../../core/services/section.service';
import { AuthService } from '../../../core/services/auth.service';
import { Section } from '../../../core/models/section.model';

@Component({
  selector: 'app-section-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <header class="header">
        <button class="back-btn" (click)="logout()">← <span class="back-label">Cerrar sesión</span></button>
        <div>
          <h1>👨‍🏫 Panel del Profesor</h1>
          <p>Seleccioná tu sección para registrar asistencia</p>
        </div>
      </header>

      <div class="sections-grid">
        @for (section of sections; track section.id) {
          <div class="section-card" (click)="select(section)">
            <div class="grade-badge">{{ section.gradeName }}</div>
            <div class="section-name">Sección {{ section.name }}</div>
            <div class="arrow">→</div>
          </div>
        }
        @if (loading) {
          <div class="loading">Cargando secciones...</div>
        }
      </div>
    </div>
  `,
  styles: [`
    .page { min-height: 100vh; background: #f0f4ff; }
    .header {
      background: #1a237e; color: white; padding: 20px 32px;
      display: flex; align-items: center; gap: 16px;
    }
    .header h1 { margin: 0; font-size: 24px; }
    .header p { margin: 4px 0 0; opacity: 0.8; font-size: 14px; }
    .back-btn {
      background: rgba(255,255,255,0.15); border: 1px solid rgba(255,255,255,0.3);
      color: white; padding: 8px 16px; border-radius: 8px; cursor: pointer;
      white-space: nowrap;
    }
    .back-btn:hover { background: rgba(255,255,255,0.25); }
    .sections-grid {
      display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 20px; padding: 32px;
    }
    .section-card {
      background: white; border-radius: 12px; padding: 28px;
      cursor: pointer; transition: all 0.2s;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      display: flex; flex-direction: column; gap: 8px;
    }
    .section-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(26,35,126,0.2);
      border-left: 4px solid #1a237e;
    }
    .grade-badge {
      font-size: 12px; font-weight: 600; color: #1976d2;
      background: #e3f2fd; padding: 4px 10px; border-radius: 20px;
      width: fit-content;
    }
    .section-name { font-size: 22px; font-weight: 700; color: #1a237e; }
    .arrow { font-size: 20px; color: #bbb; margin-top: 8px; }
    .loading { color: #888; padding: 32px; text-align: center; }
    @media (max-width: 500px) {
      .back-label { display: none; }
    }
  `]
})
export class SectionListComponent implements OnInit {
  private sectionService = inject(SectionService);
  private authService = inject(AuthService);
  private router = inject(Router);

  sections: Section[] = [];
  loading = true;

  ngOnInit() {
    this.sectionService.getAll().subscribe({
      next: (data) => { this.sections = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  select(section: Section) {
    this.router.navigate(['/professor/attendance', section.id]);
  }

  logout() {
    this.authService.logout().subscribe(() => this.router.navigate(['/login']));
  }
}
