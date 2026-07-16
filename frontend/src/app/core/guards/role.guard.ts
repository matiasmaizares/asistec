import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { TeacherRole } from '../models/teacher.model';

export function roleGuard(role: TeacherRole): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated() && authService.hasRole(role)) {
      return true;
    }
    return router.createUrlTree(['/login']);
  };
}
