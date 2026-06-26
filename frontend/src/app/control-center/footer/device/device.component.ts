import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {MatSelect} from "@angular/material/select";
import {DeviceService} from "../../../shared/device.service";
import {FormsModule} from "@angular/forms";

import {DeviceInfo} from "../../../../shared/openapi-gen";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {MatFormField, MatLabel} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-device',
  imports: [
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    FormsModule,
    MatIcon,
    RouterLink,
    MatMiniFabButton,
    MatIconButton,
    MatTooltip
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

  onConnectionToggle() {
    let device = this.selectedDevice;
    if (!device) return;
    if (this.isConnected()) {
      this.deviceService.disconnect();
    } else {
      this.deviceService.connect(device);
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
