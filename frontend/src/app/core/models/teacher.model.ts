export type TeacherRole = 'DOCENTE' | 'COORDINADOR';

export interface Teacher {
  id: string;
  fullName: string;
  email: string;
  role: TeacherRole;
}

export interface AuthResponse {
  accessToken: string;
  teacher: Teacher;
}
