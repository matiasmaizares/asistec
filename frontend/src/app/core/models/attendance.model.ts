export type AttendanceStatus = 'PRESENTE' | 'AUSENTE' | 'TARDANZA' | null;

export interface StudentAttendance {
  studentId: string;
  fullName: string;
  status: AttendanceStatus;
}

export interface RegisterAttendanceRequest {
  sectionId: string;
  date: string;
  records: { studentId: string; status: AttendanceStatus }[];
}

export interface AttendanceResponse {
  message: string;
  savedAt: string;
}

export interface SectionSummary {
  sectionId: string;
  sectionName: string;
  presentCount: number;
  absentCount: number;
  lateCount: number;
  hasAttendance: boolean;
}

export interface AttendanceHistoryEntry {
  date: string;
  status: AttendanceStatus;
  sectionId: string;
}

export interface AttendanceEvent {
  sectionId: string;
  date: string;
}

export type StreamEvent =
  | { type: 'connected' }
  | { type: 'attendance-updated'; sectionId: string; date: string };
