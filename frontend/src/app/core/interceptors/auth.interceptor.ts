import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const authReq = authService.accessToken
    ? req.clone({ setHeaders: { Authorization: `Bearer ${authService.accessToken}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint = req.url.includes('/api/v1/auth/');
      if (error.status !== 401 || isAuthEndpoint) {
        return throwError(() => error);
      }

      // 401 en un endpoint protegido: el access token expiró. Un solo intento
      // de refresh (con la cookie httpOnly) y reintento; si falla, a login.
      return authService.refresh().pipe(
        switchMap(() => {
          const retryReq = req.clone({ setHeaders: { Authorization: `Bearer ${authService.accessToken}` } });
          return next(retryReq);
        }),
        catchError((refreshError) => {
          authService.clearSession();
          router.navigate(['/login']);
          return throwError(() => refreshError);
        })
      );
    })
  );
};
