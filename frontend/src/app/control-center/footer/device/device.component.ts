import {Component, inject, OnInit, signal, ChangeDetectionStrategy} from '@angular/core';
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatSelect} from "@angular/material/select";
import {DeviceService} from "../../../shared/device.service";
import {FormsModule} from "@angular/forms";

import {DeviceInfo} from "../../../../shared/openapi-gen";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {MatFormField, MatLabel} from "@angular/material/input";
import {MatOption} from "@angular/material/core";

@Component({
  selector: 'app-device',
  imports: [
    MatSlideToggle,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    FormsModule,
    MatIcon,
    MatFabButton,
    RouterLink
],
  templateUrl: './device.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './device.component.css'
})
export class DeviceComponent implements OnInit {

  private deviceService = inject(DeviceService);
  private deviceSubscription = inject(DeviceSubscription);

  devices = signal<DeviceInfo[]>([]);
  isConnected = this.deviceService.isConnected;
  selectedDevice: DeviceInfo | null = null;

  ngOnInit() {
    this.fetchDevices();

    this.deviceSubscription.deviceDataChanged().subscribe(() => {
      this.fetchDevices();
    });
  }

  onConnectionToggle(checked: boolean) {
    let device = this.selectedDevice;
    if (!device) return;
    if (checked) {
      this.deviceService.connect(device);
    } else {
      this.deviceService.disconnect();
    }
  }

  private fetchDevices() {
    this.deviceService.loadDevices().subscribe(data => {
      this.devices.set(data);

      this.deviceSubscription.deviceConnection().subscribe(event => {
        if (event) {
          if (event.deviceInfo) {
            this.selectedDevice = this.devices().find(d => d.id === event.deviceInfo?.id) ?? null;
          } else {
            this.selectedDevice = null;
          }
        }
      })
    })
  }
}
