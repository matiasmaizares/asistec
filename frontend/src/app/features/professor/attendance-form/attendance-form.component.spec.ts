import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { AttendanceFormComponent } from './attendance-form.component';
import { AttendanceService } from '../../../core/services/attendance.service';
import { SectionService } from '../../../core/services/section.service';

describe('AttendanceFormComponent', () => {
  let fixture: ComponentFixture<AttendanceFormComponent>;
  let component: AttendanceFormComponent;

  const mockStudents = [
    { studentId: 'id-1', fullName: 'Lucas Romero',    status: 'PRESENTE' as const },
    { studentId: 'id-2', fullName: 'Valentina Torres', status: 'AUSENTE'  as const },
    { studentId: 'id-3', fullName: 'Mateo García',    status: null },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttendanceFormComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'section-uuid' } } },
        },
        {
          provide: AttendanceService,
          useValue: {
            getForSection: () => of(mockStudents),
            register: () => of({ message: 'Asistencia registrada correctamente', savedAt: '' }),
          },
        },
        {
          provide: SectionService,
          useValue: { getAll: () => of([{ id: 'section-uuid', name: '3er Grado A', gradeName: '3er Grado' }]) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AttendanceFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: lista de estudiantes con estados correctamente renderizados
  it('debe renderizar la lista de estudiantes con sus estados', () => {
    const rows = fixture.nativeElement.querySelectorAll('.student-row');
    expect(rows.length).toBe(3);

    const names = Array.from(rows).map((r: any) =>
      r.querySelector('.student-name')?.textContent?.trim()
    );
    expect(names).toContain('Lucas Romero');
    expect(names).toContain('Valentina Torres');
    expect(names).toContain('Mateo García');
  });

  it('debe marcar el botón activo según el estado del estudiante', () => {
    const rows = fixture.nativeElement.querySelectorAll('.student-row');
    const lucasRow = rows[0];
    const activeBtn = lucasRow.querySelector('.status-btn.active');
    expect(activeBtn?.textContent?.trim()).toBe('Presente');
  });

  // Test 2: botón guardar deshabilitado si no hay cambios
  it('debe deshabilitar el botón guardar cuando no hay cambios', () => {
    const saveBtn = fixture.nativeElement.querySelector('.save-btn');
    expect(saveBtn.disabled).toBeTrue();
  });

  it('debe deshabilitar el botón guardar si hay cambios pero algún alumno sigue en null', () => {
    component.setStatus('id-1', 'TARDANZA'); // cambia id-1, id-3 sigue null
    fixture.detectChanges();
    const saveBtn = fixture.nativeElement.querySelector('.save-btn');
    expect(saveBtn.disabled).toBeTrue();
  });

  it('debe habilitar el botón guardar cuando hay cambios y todos los alumnos están marcados', () => {
    component.setStatus('id-1', 'TARDANZA'); // cambia respecto al original
    component.setStatus('id-3', 'AUSENTE');  // resuelve el null
    fixture.detectChanges();
    const saveBtn = fixture.nativeElement.querySelector('.save-btn');
    expect(saveBtn.disabled).toBeFalse();
  });
});
