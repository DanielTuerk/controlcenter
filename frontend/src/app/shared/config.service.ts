import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SnackBar} from '../control-center/common/snack-bar.component';
import {catchError, tap} from 'rxjs/operators';
import {EMPTY, Observable, of} from 'rxjs';

export const KEY_CONSTRUCTION_DEFAULT = 'construction.default';

@Injectable({providedIn: 'root'})
export class ConfigService {
  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  loadConfigValue(key: string): Observable<string | null> {
    return this.httpClient.get(`/api/config/${key}`, {responseType: 'text'}).pipe(
      catchError((err: any) => {
        if (err.status === 404) {
          return of(null);
        }
        this.snackBar.showError(`can't load ${key}: ${err.message}`);
        return EMPTY;
      })
    );
  }

  saveConfigValue(key: string, value: string): Observable<void> {
    return this.httpClient.put<void>(`/api/config/${key}`, value, {
      headers: {
        'Content-Type': 'text/plain'
      }
    }).pipe(
      tap(() => this.snackBar.showSuccess(`saved ${key}`)),
      catchError((err: any) => {
        this.snackBar.showError(`can't save ${key} (${value}): ${err.message}`);
        return EMPTY;
      })
    );
  }
}
