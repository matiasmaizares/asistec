import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { StreamEvent } from '../models/attendance.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AttendanceStreamService {
  private authService = inject(AuthService);

  stream(): Observable<StreamEvent> {
    return new Observable((observer) => {
      // En dev el proxy de webpack bufferiza SSE; conectar directo al backend (CORS ya permite localhost:4200).
      // En producción window.location.hostname no es 'localhost', por lo que usa URL relativa sin proxy.
      const base = window.location.hostname === 'localhost'
        ? 'http://localhost:8080'
        : 'https://assitec-backend-production.up.railway.app';

      // EventSource nativo no puede mandar headers custom (Authorization), así
      // que el access token viaja por query param — el backend solo lo acepta
      // en este endpoint puntual. Si el token expira mientras la pestaña queda
      // abierta (>15 min), el reconnect automático del browser va a fallar
      // hasta que se recargue la página — trade-off aceptado por simplicidad.
      const token = this.authService.accessToken ?? '';
      const source = new EventSource(`${base}/api/v1/reports/stream?token=${encodeURIComponent(token)}`);

      source.addEventListener('connected', () => observer.next({ type: 'connected' }));

      source.addEventListener('attendance-updated', (e: MessageEvent) => {
        observer.next({ type: 'attendance-updated', ...JSON.parse(e.data) });
      });

      source.onerror = () => observer.error('SSE connection error');

      return () => source.close();
    });
  }
}
