import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, firstValueFrom, tap } from 'rxjs';
import { AuthResponse, Teacher, TeacherRole } from '../models/teacher.model';

/**
 * El access token vive solo en memoria (signal), nunca en localStorage: si un XSS
 * roba algo, roba un token que expira en minutos, no uno persistente. El refresh
 * token vive en una cookie httpOnly que este servicio ni siquiera puede leer —
 * el browser la manda sola en cada request al mismo origin (login/refresh/logout).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  private accessTokenSignal = signal<string | null>(null);
  private teacherSignal = signal<Teacher | null>(null);

  readonly teacher = this.teacherSignal.asReadonly();

  get accessToken(): string | null {
    return this.accessTokenSignal();
  }

  isAuthenticated(): boolean {
    return this.accessTokenSignal() !== null;
  }

  hasRole(role: TeacherRole): boolean {
    return this.teacherSignal()?.role === role;
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/v1/auth/login', { email, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  refresh(): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/v1/auth/refresh', {})
      .pipe(tap((res) => this.setSession(res)));
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/v1/auth/logout', {}).pipe(tap(() => this.clearSession()));
  }

  clearSession(): void {
    this.accessTokenSignal.set(null);
    this.teacherSignal.set(null);
  }

  /**
   * Se llama una vez al arrancar la app (ver APP_INITIALIZER en app.config.ts):
   * intenta canjear la cookie de refresh por un access token nuevo, para no
   * pedir login de nuevo en cada F5. Si no hay cookie válida, no rompe el arranque.
   */
  tryRestoreSession(): Promise<void> {
    return firstValueFrom(this.refresh())
      .then(() => undefined)
      .catch(() => this.clearSession());
  }

  private setSession(res: AuthResponse): void {
    this.accessTokenSignal.set(res.accessToken);
    this.teacherSignal.set(res.teacher);
  }
}
