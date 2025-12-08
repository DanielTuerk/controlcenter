import {inject} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {DeviceInfo} from "../../shared/openapi-gen";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

export class BusService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  railVoltage() {
    return this.httpClient.post<null>('/api/bus/railvoltage', null)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't toggle railvoltage: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }



}
