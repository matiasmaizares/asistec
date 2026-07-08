import { Routes } from '@angular/router';
import { RoleSelectorComponent } from './features/role-selector/role-selector.component';

export const routes: Routes = [
  { path: '', component: RoleSelectorComponent },
  {
    path: 'professor',
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
    loadComponent: () =>
      import('./features/coordinator/dashboard/dashboard.component').then(
        m => m.DashboardComponent
      ),
  },
  { path: '**', redirectTo: '' },
];
