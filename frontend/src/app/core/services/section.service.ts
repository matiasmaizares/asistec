import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Section } from '../models/section.model';

@Injectable({ providedIn: 'root' })
export class SectionService {
  private http = inject(HttpClient);

  getAll(): Observable<Section[]> {
    return this.http.get<Section[]>('/api/v1/sections');
  }
}
