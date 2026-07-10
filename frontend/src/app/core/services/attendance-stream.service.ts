import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StreamEvent } from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceStreamService {
  stream(): Observable<StreamEvent> {
    return new Observable((observer) => {
      // En dev el proxy de webpack bufferiza SSE; conectar directo al backend (CORS ya permite localhost:4200).
      // En producción window.location.hostname no es 'localhost', por lo que usa URL relativa sin proxy.
      const base = window.location.hostname === 'localhost'
        ? 'http://localhost:8080'
        : 'https://assitec-backend-production.up.railway.app';
      const source = new EventSource(`${base}/api/v1/reports/stream`);

      source.addEventListener('connected', () => observer.next({ type: 'connected' }));

      source.addEventListener('attendance-updated', (e: MessageEvent) => {
        observer.next({ type: 'attendance-updated', ...JSON.parse(e.data) });
      });

      source.onerror = () => observer.error('SSE connection error');

      return () => source.close();
    });
  }
}
