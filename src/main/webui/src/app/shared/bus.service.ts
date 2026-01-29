import {inject} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

export class BusService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  railVoltage() {
    return this.callBusAction('railvoltage')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't toggle railvoltage: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  busFormat() {
    return this.callBusAction('system-format')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't switch system format: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  private callBusAction(path: string) {
    return this.httpClient.post<null>('/api/bus/' + path, null)
  }
}
