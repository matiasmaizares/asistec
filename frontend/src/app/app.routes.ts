import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'professor',
    canActivate: [roleGuard('DOCENTE')],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/professor/section-list/section-list.component').then(
            m => m.SectionListComponent
          ),
      },
      {
        path: 'attendance/:sectionId',
        loadComponent: () =>
          import('./features/professor/attendance-form/attendance-form.component').then(
            m => m.AttendanceFormComponent
          ),
      },
    ],
  },
  {
    path: 'coordinator',
    canActivate: [roleGuard('COORDINADOR')],
    loadComponent: () =>
      import('./features/coordinator/dashboard/dashboard.component').then(
        m => m.DashboardComponent
      ),
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
