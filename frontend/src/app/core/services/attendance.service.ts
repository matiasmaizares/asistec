import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AttendanceHistoryEntry,
  AttendanceResponse,
  RegisterAttendanceRequest,
  SectionSummary,
  StudentAttendance,
} from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private http = inject(HttpClient);

  getForSection(sectionId: string, date: string): Observable<StudentAttendance[]> {
    return this.http.get<StudentAttendance[]>(`/api/v1/attendance/${sectionId}`, {
      params: { date },
    });
  }

  register(request: RegisterAttendanceRequest): Observable<AttendanceResponse> {
    return this.http.post<AttendanceResponse>('/api/v1/attendance', request);
  }

  getDailySummary(date: string): Observable<SectionSummary[]> {
    return this.http.get<SectionSummary[]>('/api/v1/reports/daily', {
      params: { date },
    });
  }

  getStudentHistory(studentId: string, from: string, to: string): Observable<AttendanceHistoryEntry[]> {
    return this.http.get<AttendanceHistoryEntry[]>(
      `/api/v1/reports/students/${studentId}/history`,
      { params: { from, to } }
    );
  }

  getPendingSections(date: string): Observable<{ id: string; name: string }[]> {
    return this.http.get<{ id: string; name: string }[]>(
      '/api/v1/reports/sections/pending',
      { params: { date } }
    );
  }
}
