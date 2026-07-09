import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '../../../core/services/attendance.service';
import { SectionService } from '../../../core/services/section.service';
import { AttendanceStatus, StudentAttendance } from '../../../core/models/attendance.model';

@Component({
  selector: 'app-attendance-form',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <header class="header">
        <div class="header-inner">
          <button class="back-btn" (click)="goBack()">← <span class="back-label">Volver</span></button>
          <div>
            <h1>{{ sectionName }}</h1>
            <p>Asistencia del {{ formattedDate }}</p>
          </div>
          <div class="header-actions">
            <button class="save-btn" [disabled]="!canSave || saving" (click)="save()">
              {{ saving ? '⏳' : '💾' }} <span class="save-label">{{ saving ? 'Guardando...' : 'Guardar asistencia' }}</span>
            </button>
          </div>
        </div>
      </header>

      @if (successMsg) {
        <div class="alert alert-success">✅ {{ successMsg }}</div>
      }
      @if (errorMsg) {
        <div class="alert alert-error">❌ {{ errorMsg }}</div>
      }

      <div class="student-list">
        @if (loading) {
          <div class="loading">Cargando alumnos...</div>
        }
        @for (student of students; track student.studentId) {
          <div class="student-row" [class.modified]="isModified(student.studentId)">
            <div class="student-name">{{ student.fullName }}</div>
            <div class="status-buttons">
              <button
                class="status-btn presente"
                [class.active]="getDraft(student.studentId) === 'PRESENTE'"
                (click)="setStatus(student.studentId, 'PRESENTE')">
                Presente
              </button>
              <button
                class="status-btn tardanza"
                [class.active]="getDraft(student.studentId) === 'TARDANZA'"
                (click)="setStatus(student.studentId, 'TARDANZA')">
                Tardanza
              </button>
              <button
                class="status-btn ausente"
                [class.active]="getDraft(student.studentId) === 'AUSENTE'"
                (click)="setStatus(student.studentId, 'AUSENTE')">
                Ausente
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .page { min-height: 100vh; background: #f0f4ff; }
    .header { background: #1a237e; color: white; padding: 20px 32px; }
    .header-inner {
      display: flex; align-items: center; gap: 16px;
      max-width: 860px; margin: 0 auto;
    }
    .header-inner > div:nth-child(2) { flex: 1; }
    .header h1 { margin: 0; font-size: 22px; }
    .header p { margin: 4px 0 0; opacity: 0.8; font-size: 13px; }
    .back-btn {
      background: rgba(255,255,255,0.15); border: 1px solid rgba(255,255,255,0.3);
      color: white; padding: 8px 16px; border-radius: 8px; cursor: pointer;
    }
    .back-btn:hover { background: rgba(255,255,255,0.25); }
    .save-btn {
      background: #43a047; color: white; border: none;
      padding: 10px 24px; border-radius: 8px; font-size: 15px;
      font-weight: 600; cursor: pointer; transition: all 0.2s;
    }
    .save-btn:hover:not(:disabled) { background: #388e3c; }
    .save-btn:disabled { background: #a5d6a7; cursor: not-allowed; opacity: 0.7; }
    .alert {
      margin: 16px auto; padding: 12px 20px; border-radius: 8px; font-weight: 500;
      max-width: 860px;
    }
    .alert-success { background: #e8f5e9; color: #2e7d32; border: 1px solid #a5d6a7; }
    .alert-error { background: #ffebee; color: #c62828; border: 1px solid #ef9a9a; }
    .student-list {
      padding: 24px 32px; display: flex; flex-direction: column; gap: 12px;
      max-width: 860px; margin: 0 auto;
    }
    .student-row {
      background: white; border-radius: 10px; padding: 16px 20px;
      display: flex; align-items: center; justify-content: space-between;
      box-shadow: 0 1px 4px rgba(0,0,0,0.06);
      border-left: 4px solid transparent; transition: border-color 0.2s;
    }
    .student-row.modified { border-left-color: #fb8c00; }
    .student-name { font-size: 16px; font-weight: 500; color: #333; }
    .status-buttons { display: flex; gap: 8px; }
    .status-btn {
      padding: 7px 16px; border-radius: 20px; border: 2px solid;
      font-size: 13px; font-weight: 600; cursor: pointer;
      transition: all 0.15s; background: white;
    }
    .status-btn.presente { border-color: #43a047; color: #43a047; }
    .status-btn.presente.active { background: #43a047; color: white; }
    .status-btn.tardanza { border-color: #fb8c00; color: #fb8c00; }
    .status-btn.tardanza.active { background: #fb8c00; color: white; }
    .status-btn.ausente { border-color: #e53935; color: #e53935; }
    .status-btn.ausente.active { background: #e53935; color: white; }
    .loading { text-align: center; color: #888; padding: 40px; }
    @media (max-width: 500px) {
      .student-list { padding: 16px; }
      .student-row { align-items: flex-start; gap: 12px; }
      .status-buttons { flex-direction: column; }
      .status-btn { width: 100px; text-align: center; }
      .back-label { display: none; }
      .save-label { display: none; }
    }
  `]
})
export class AttendanceFormComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private attendanceService = inject(AttendanceService);
  private sectionService = inject(SectionService);

  sectionId = '';
  sectionName = '';
  today = new Date();
  get formattedDate(): string {
    return this.today.toLocaleDateString('es-AR', { weekday: 'long', day: 'numeric', month: 'long' });
  }
  students: StudentAttendance[] = [];
  draft = new Map<string, AttendanceStatus>();
  original = new Map<string, AttendanceStatus>();
  loading = true;
  saving = false;
  successMsg = '';
  errorMsg = '';

  get hasChanges(): boolean {
    for (const [id, status] of this.draft.entries()) {
      if (status !== this.original.get(id)) return true;
    }
    return false;
  }

  get canSave(): boolean {
    if (!this.hasChanges) return false;
    for (const status of this.draft.values()) {
      if (!status) return false;
    }
    return true;
  }

  private toLocalDateStr(date: Date): string {
    return [
      date.getFullYear(),
      String(date.getMonth() + 1).padStart(2, '0'),
      String(date.getDate()).padStart(2, '0')
    ].join('-');
  }

  ngOnInit() {
    this.sectionId = this.route.snapshot.paramMap.get('sectionId')!;
    this.loadSectionName();
    this.loadAttendance();
  }

  private loadSectionName() {
    this.sectionService.getAll().subscribe(sections => {
      const s = sections.find(s => s.id === this.sectionId);
      this.sectionName = s ? s.name : 'Sección';
    });
  }

  private loadAttendance() {
    const dateStr = this.toLocalDateStr(this.today);
    this.attendanceService.getForSection(this.sectionId, dateStr).subscribe({
      next: (data) => {
        this.students = data;
        data.forEach(s => {
          this.draft.set(s.studentId, s.status);
          this.original.set(s.studentId, s.status);
        });
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  getDraft(studentId: string): AttendanceStatus {
    return this.draft.get(studentId) ?? null;
  }

  isModified(studentId: string): boolean {
    return this.draft.get(studentId) !== this.original.get(studentId);
  }

  setStatus(studentId: string, status: AttendanceStatus) {
    this.draft.set(studentId, status);
    this.successMsg = '';
    this.errorMsg = '';
  }

  save() {
    this.saving = true;
    this.successMsg = '';
    this.errorMsg = '';

    const dateStr = this.toLocalDateStr(this.today);
    const records = Array.from(this.draft.entries()).map(([studentId, status]) => ({
      studentId,
      status,
    }));

    this.attendanceService.register({ sectionId: this.sectionId, date: dateStr, records }).subscribe({
      next: (res) => {
        this.saving = false;
        this.successMsg = res.message;
        this.draft.forEach((status, id) => this.original.set(id, status));
      },
      error: (err) => {
        this.saving = false;
        this.errorMsg = err.error?.detail ?? 'Error al guardar la asistencia';
      }
    });
  }

  goBack() {
    this.router.navigate(['/professor']);
  }
}
