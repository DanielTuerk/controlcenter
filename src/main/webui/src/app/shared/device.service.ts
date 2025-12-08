import {inject} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {DeviceInfo} from "../../shared/openapi-gen";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

export class DeviceService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  loadDevices() {
    return this.httpClient.get<DeviceInfo[]>('/api/devices')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load devices: ${err.message}`);
        return EMPTY
      })
    );
  }

  connect(deviceInfo: DeviceInfo) {
    return this.callDeviceFunction(`${deviceInfo.id}/connect`)
  }

  disconnect() {
    return this.callDeviceFunction('disconnect')
  }

  private callDeviceFunction(path: string) {
    return this.httpClient.post<DeviceInfo[]>(`/api/devices/${path}`, null)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't call ${path}: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

}
