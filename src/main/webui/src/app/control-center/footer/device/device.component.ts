import {Component, inject, OnInit, signal} from '@angular/core';
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {DeviceService} from "../../../shared/device.service";
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {DeviceInfo, TYPE} from "../../../../shared/openapi-gen";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-device',
  imports: [
    MatSlideToggle,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    FormsModule,
    NgForOf,
    MatIcon,
    MatFabButton,
    MatButton,
    RouterLink
  ],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css'
})
export class DeviceComponent implements OnInit {

  private deviceService = inject(DeviceService);
  private deviceSubscription = inject(DeviceSubscription);

  devices = signal<DeviceInfo[]>([]);
  // selectedDeviceId:DeviceInfo|null=null;
  isConnected = false;
  selectedDevice: DeviceInfo | null = null;

  ngOnInit() {
    // this.deviceSubscription.deviceInfo().subscribe(event => {
    //   this.fetchDevices();
    // });
    this.fetchDevices();
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
        this.isConnected = false;
        if (event) {
          if (event.deviceInfo) {
            this.selectedDevice = this.devices().find(d => d.id === event.deviceInfo?.id) ?? null;
          } else {
            this.selectedDevice = null;
          }
          if (event.eventType == TYPE.Connected) {
            this.isConnected = true;
          }
        }
      })
    })
  }
}
