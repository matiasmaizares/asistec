import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StudentHistoryComponent } from '../student-history/student-history.component';
import { Subscription } from 'rxjs';
import { AttendanceService } from '../../../core/services/attendance.service';
import { AttendanceStreamService } from '../../../core/services/attendance-stream.service';
import { SectionSummary } from '../../../core/models/attendance.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StudentHistoryComponent],
  template: `
    <div class="page">
      <header class="header">
        <button class="back-btn" (click)="goBack()">← <span class="back-label">Salir</span></button>
        <div>
          <h1>📊 Panel del Coordinador</h1>
          <p>Asistencia del día — {{ formattedDate }}</p>
        </div>
        <div class="live-badge" [class.connected]="connected">
          <span class="dot"></span>
          {{ connected ? 'En vivo' : 'Conectando...' }}
        </div>
      </header>

      <nav class="tabs">
        <button [class.active]="tab === 'summary'" (click)="tab = 'summary'">Resumen del día</button>
        <button [class.active]="tab === 'pending'" (click)="tab = 'pending'; loadPending()">Secciones pendientes</button>
        <button [class.active]="tab === 'history'" (click)="tab = 'history'">Historial de alumno</button>
      </nav>

      <div class="content">
        @if (tab === 'summary') {
          <div class="summary-grid">
            @for (s of summary; track s.sectionId) {
              <div class="summary-card" [class.no-data]="!s.hasAttendance">
                <div class="card-header">
                  <span class="section-name">{{ s.sectionName }}</span>
                  @if (!s.hasAttendance) {
                    <span class="pending-tag">Sin registro</span>
                  }
                </div>
                <div class="stats">
                  <div class="stat presente">
                    <div class="stat-value">{{ s.presentCount }}</div>
                    <div class="stat-label">Presentes</div>
                  </div>
                  <div class="stat ausente">
                    <div class="stat-value">{{ s.absentCount }}</div>
                    <div class="stat-label">Ausentes</div>
                  </div>
                  <div class="stat tardanza">
                    <div class="stat-value">{{ s.lateCount }}</div>
                    <div class="stat-label">Tardanzas</div>
                  </div>
                </div>
              </div>
            }
          </div>
        }

        @if (tab === 'pending') {
          <div class="pending-list">
            <h2>Secciones sin asistencia hoy</h2>
            @if (pendingSections.length === 0) {
              <div class="empty">✅ Todas las secciones registraron asistencia hoy</div>
            }
            @for (s of pendingSections; track s.id) {
              <div class="pending-item">
                <span class="warning-icon">⚠️</span>
                <span>{{ s.name }}</span>
              </div>
            }
          </div>
        }

        @if (tab === 'history') {
          <app-student-history />
        }
      </div>
    </div>
  `,
  styles: [`
    .page { min-height: 100vh; background: #f0f4ff; }
    .header {
      background: #1b5e20; color: white; padding: 20px 32px;
      display: flex; align-items: center; gap: 16px;
    }
    .header > div:nth-child(2) { flex: 1; }
    .header h1 { margin: 0; font-size: 22px; }
    .header p { margin: 4px 0 0; opacity: 0.8; font-size: 13px; }
    .back-btn {
      background: rgba(255,255,255,0.15); border: 1px solid rgba(255,255,255,0.3);
      color: white; padding: 8px 16px; border-radius: 8px; cursor: pointer;
    }
    .back-btn:hover { background: rgba(255,255,255,0.25); }
    .live-badge {
      display: flex; align-items: center; gap: 6px;
      padding: 6px 14px; border-radius: 20px;
      background: rgba(255,255,255,0.15); font-size: 13px; font-weight: 600;
    }
    .live-badge.connected { background: rgba(76,175,80,0.3); }
    .dot {
      width: 8px; height: 8px; border-radius: 50%; background: #ef5350;
    }
    .live-badge.connected .dot { background: #69f0ae; animation: pulse 1.5s infinite; }
    @keyframes pulse {
      0%, 100% { opacity: 1; } 50% { opacity: 0.3; }
    }
    .tabs {
      display: flex; gap: 0; background: white;
      border-bottom: 2px solid #e0e0e0; padding: 0 32px;
    }
    .tabs button {
      padding: 14px 24px; border: none; background: none;
      font-size: 14px; font-weight: 500; color: #666;
      cursor: pointer; border-bottom: 3px solid transparent; margin-bottom: -2px;
    }
    .tabs button.active { color: #1b5e20; border-bottom-color: #1b5e20; font-weight: 700; }
    .content { padding: 32px; }
    .summary-grid {
      display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px;
    }
    .summary-card {
      background: white; border-radius: 12px; padding: 24px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08); border-top: 4px solid #43a047;
    }
    .summary-card.no-data { border-top-color: #bdbdbd; opacity: 0.7; }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .section-name { font-size: 18px; font-weight: 700; color: #1a237e; }
    .pending-tag {
      font-size: 11px; font-weight: 600; background: #fff3e0; color: #e65100;
      padding: 3px 8px; border-radius: 10px;
    }
    .stats { display: flex; gap: 12px; }
    .stat { flex: 1; text-align: center; padding: 12px 8px; border-radius: 8px; }
    .stat-value { font-size: 28px; font-weight: 800; }
    .stat-label { font-size: 11px; font-weight: 600; margin-top: 2px; text-transform: uppercase; }
    .stat.presente { background: #e8f5e9; }
    .stat.presente .stat-value { color: #2e7d32; }
    .stat.presente .stat-label { color: #4caf50; }
    .stat.ausente { background: #ffebee; }
    .stat.ausente .stat-value { color: #c62828; }
    .stat.ausente .stat-label { color: #ef5350; }
    .stat.tardanza { background: #fff3e0; }
    .stat.tardanza .stat-value { color: #e65100; }
    .stat.tardanza .stat-label { color: #fb8c00; }
    .pending-list h2 { margin: 0 0 20px; color: #333; }
    .pending-item {
      background: white; padding: 16px 20px; border-radius: 8px;
      margin-bottom: 10px; display: flex; align-items: center; gap: 12px;
      font-size: 16px; font-weight: 500; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
    }
    .empty { background: white; padding: 32px; border-radius: 8px; text-align: center; color: #666; }
    @media (max-width: 500px) {
      .back-label { display: none; }
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private attendanceService = inject(AttendanceService);
  private streamService = inject(AttendanceStreamService);
  private router = inject(Router);

  today = new Date();
  get formattedDate(): string {
    return this.today.toLocaleDateString('es-AR', { weekday: 'long', day: 'numeric', month: 'long' });
  }
  tab: 'summary' | 'pending' | 'history' = 'summary';
  summary: SectionSummary[] = [];
  pendingSections: { id: string; name: string }[] = [];
  connected = false;
  private streamSub?: Subscription;

  ngOnInit() {
    this.loadSummary();
    this.connectStream();
  }

  ngOnDestroy() {
    this.streamSub?.unsubscribe();
  }

  private toLocalDateStr(date: Date): string {
    return [
      date.getFullYear(),
      String(date.getMonth() + 1).padStart(2, '0'),
      String(date.getDate()).padStart(2, '0')
    ].join('-');
  }

  private loadSummary() {
    this.attendanceService.getDailySummary(this.toLocalDateStr(this.today)).subscribe(data => {
      this.summary = data;
    });
  }

  loadPending() {
    this.attendanceService.getPendingSections(this.toLocalDateStr(this.today)).subscribe(data => {
      this.pendingSections = data;
    });
  }

  private connectStream() {
    this.streamSub = this.streamService.stream().subscribe({
      next: (event) => {
        if (event.type === 'connected') {
          this.connected = true;
          return;
        }
        this.loadSummary();
        if (this.tab === 'pending') this.loadPending();
      },
      error: () => { this.connected = false; }
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }
}
