import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService } from '../../../core/services/attendance.service';
import { SectionService } from '../../../core/services/section.service';
import { AttendanceHistoryEntry } from '../../../core/models/attendance.model';

@Component({
  selector: 'app-student-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="history-container">
      <h2>Historial de alumno</h2>

      <div class="filters">
        <div class="filter-group">
          <label>Sección</label>
          <select [(ngModel)]="selectedSectionId" (change)="onSectionChange()">
            <option value="">-- Seleccioná una sección --</option>
            @for (s of sections; track s.id) {
              <option [value]="s.id">{{ s.name }}</option>
            }
          </select>
        </div>

        <div class="filter-group">
          <label>Alumno</label>
          <select [(ngModel)]="selectedStudentId" [disabled]="!selectedSectionId">
            <option value="">-- Seleccioná un alumno --</option>
            @for (s of studentsInSection; track s.studentId) {
              <option [value]="s.studentId">{{ s.fullName }}</option>
            }
          </select>
        </div>

        <div class="filter-group">
          <label>Desde</label>
          <input type="date" [(ngModel)]="from" />
        </div>

        <div class="filter-group">
          <label>Hasta</label>
          <input type="date" [(ngModel)]="to" />
        </div>

        <button class="search-btn"
          [disabled]="!selectedStudentId || !from || !to"
          (click)="search()">
          Buscar
        </button>
      </div>

      @if (errorMsg) {
        <div class="alert-error">❌ {{ errorMsg }}</div>
      }

      @if (history.length > 0) {
        <table class="history-table">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            @for (entry of history; track entry.date) {
              <tr>
                <td>{{ entry.date | date:'dd/MM/yyyy' }}</td>
                <td>
                  <span class="badge" [class]="entry.status?.toLowerCase()">
                    {{ entry.status }}
                  </span>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }

      @if (searched && history.length === 0 && !errorMsg) {
        <div class="empty">Sin registros en el período seleccionado</div>
      }
    </div>
  `,
  styles: [`
    .history-container h2 { color: #333; margin: 0 0 20px; }
    .filters {
      display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end;
      background: white; padding: 20px; border-radius: 10px;
      margin-bottom: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
    }
    .filter-group { display: flex; flex-direction: column; gap: 4px; }
    .filter-group label { font-size: 12px; font-weight: 600; color: #666; }
    select, input[type="date"] {
      padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px;
      font-size: 14px; min-width: 180px;
    }
    .search-btn {
      padding: 9px 24px; background: #1b5e20; color: white;
      border: none; border-radius: 6px; font-size: 14px;
      font-weight: 600; cursor: pointer;
    }
    .search-btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .search-btn:hover:not(:disabled) { background: #2e7d32; }
    .history-table {
      width: 100%; border-collapse: collapse; background: white;
      border-radius: 10px; overflow: hidden;
      box-shadow: 0 1px 4px rgba(0,0,0,0.06);
    }
    th { background: #f5f5f5; padding: 12px 20px; text-align: left; font-size: 13px; color: #555; }
    td { padding: 12px 20px; border-top: 1px solid #f0f0f0; font-size: 14px; }
    .badge {
      padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 700;
    }
    .badge.presente { background: #e8f5e9; color: #2e7d32; }
    .badge.ausente { background: #ffebee; color: #c62828; }
    .badge.tardanza { background: #fff3e0; color: #e65100; }
    .empty { background: white; padding: 32px; text-align: center; color: #888; border-radius: 8px; }
    .alert-error { background: #ffebee; color: #c62828; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; }
  `]
})
export class StudentHistoryComponent {
  private attendanceService = inject(AttendanceService);
  private sectionService = inject(SectionService);

  sections: { id: string; name: string }[] = [];
  studentsInSection: { studentId: string; fullName: string; status: any }[] = [];
  selectedSectionId = '';
  selectedStudentId = '';
  from = '';
  to = '';
  history: AttendanceHistoryEntry[] = [];
  searched = false;
  errorMsg = '';

  constructor() {
    this.sectionService.getAll().subscribe(s => this.sections = s);
  }

  onSectionChange() {
    this.selectedStudentId = '';
    this.studentsInSection = [];
    if (!this.selectedSectionId) return;
    const today = new Date().toISOString().split('T')[0];
    this.attendanceService.getForSection(this.selectedSectionId, today).subscribe(
      data => this.studentsInSection = data
    );
  }

  search() {
    this.errorMsg = '';
    this.searched = false;
    this.attendanceService.getStudentHistory(this.selectedStudentId, this.from, this.to).subscribe({
      next: (data) => { this.history = data; this.searched = true; },
      error: (err) => { this.errorMsg = err.error?.detail ?? 'Error al buscar historial'; }
    });
  }
}
