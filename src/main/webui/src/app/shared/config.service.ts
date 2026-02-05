import {inject} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {catchError} from "rxjs/operators";
import {EMPTY, of} from "rxjs";

export const KEY_CONSTRUCTION_SHOW_WELCOME = 'construction.show-welcome';
export const KEY_CONSTRUCTION_DEFAULT = 'construction.default';

export class ConfigService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  loadConfigValue(key: string) {
    return this.httpClient.get('/api/config/' + key)
    .pipe(
      catchError((err: any) => {
        if (err.status === 404) {
          return of(null);
        }
        this.snackBar.showError(`can't load ${key}: ${err.message}`);
        return EMPTY;
      })
    )
  }

  saveConfigValue(key: string, value: string) {
    return this.httpClient.put('/api/config/' + key, value)
    .subscribe({
      next: () => this.snackBar.showSuccess(`saved ${key}`),
      error: (err) => this.snackBar.showError(`can't save ${key} (${value}): ${err.message}`)
    });
  }


}
