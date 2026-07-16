import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, NEVER } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { AttendanceService } from '../../../core/services/attendance.service';
import { AttendanceStreamService } from '../../../core/services/attendance-stream.service';
import { AuthService } from '../../../core/services/auth.service';

describe('DashboardComponent — resumen del coordinador', () => {
  let fixture: ComponentFixture<DashboardComponent>;
  let component: DashboardComponent;

  const mockSummary = [
    { sectionId: 's1', sectionName: '3er Grado A', presentCount: 4, absentCount: 1, lateCount: 1, hasAttendance: true  },
    { sectionId: 's2', sectionName: '3er Grado B', presentCount: 5, absentCount: 0, lateCount: 1, hasAttendance: true  },
    { sectionId: 's3', sectionName: '4to Grado A', presentCount: 0, absentCount: 0, lateCount: 0, hasAttendance: false },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        {
          provide: AttendanceService,
          useValue: {
            getDailySummary: () => of(mockSummary),
            getPendingSections: () => of([]),
          },
        },
        {
          provide: AttendanceStreamService,
          useValue: { stream: () => NEVER },
        },
        {
          provide: AuthService,
          useValue: { logout: () => of(undefined) },
        },
        {
          provide: Router,
          useValue: { navigate: jasmine.createSpy() },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 3: el resumen del coordinador refleja los totales correctos
  it('debe mostrar los totales correctos por sección', () => {
    const cards = fixture.nativeElement.querySelectorAll('.summary-card');
    expect(cards.length).toBe(3);

    const firstCard = cards[0];
    const values = Array.from(firstCard.querySelectorAll('.stat-value')).map(
      (el: any) => el.textContent?.trim()
    );
    expect(values).toEqual(['4', '1', '1']);
  });

  it('debe marcar con clase no-data las secciones sin asistencia', () => {
    const cards = fixture.nativeElement.querySelectorAll('.summary-card');
    const noDataCards = fixture.nativeElement.querySelectorAll('.summary-card.no-data');
    expect(noDataCards.length).toBe(1);
    expect(cards[2].classList).toContain('no-data');
  });

  it('debe mostrar el badge "Sin registro" en secciones sin asistencia', () => {
    const pendingTags = fixture.nativeElement.querySelectorAll('.pending-tag');
    expect(pendingTags.length).toBe(1);
    expect(pendingTags[0].textContent?.trim()).toBe('Sin registro');
  });
});
