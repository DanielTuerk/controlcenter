import {inject, Injectable, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {AvailableDevice, DeviceInfo, Scenario} from "../../shared/openapi-gen";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {DeviceSubscription} from "./websocket/device.subscription";

@Injectable({
  providedIn: 'root',
})
export class DeviceService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  private deviceSubscription = inject(DeviceSubscription);
  private _isConnected = signal(false);
  public readonly isConnected = this._isConnected.asReadonly();

  init() {
    this.deviceSubscription.deviceConnection().subscribe($event => {
      console.log("device connection changed", {device: $event});
      this._isConnected.set($event.connected!);
    });
  }

  loadDevices() {
    return this.httpClient.get<DeviceInfo[]>('/api/devices')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load devices: ${err.message}`);
        return EMPTY
      })
    );
  }

  loadDevice(deviceId: Number) {
    return this.httpClient.get<Scenario>('/api/devices/' + deviceId);
  }

  availableDevices() {
    return this.httpClient.get<AvailableDevice[]>('/api/devices/available');
  }

  createDevice(device: DeviceInfo) {
    return this.httpClient.post('/api/devices/', device)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't create device: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveDevice(device: DeviceInfo) {
    return this.httpClient.put('/api/devices/' + device.id, device)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save device ${device.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteDevice(deviceId: Number) {
    return this.httpClient.delete('/api/devices/' + deviceId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`device ${deviceId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete device ${deviceId}: ${error.message}`);
      }
    });
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
