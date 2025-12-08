import {Component, inject, OnInit} from '@angular/core';
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {BusSubscription} from "../../../shared/websocket/bus.subscription";
import {BusService} from "../../../shared/bus.service";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {TYPE} from "../../../../shared/openapi-gen";

@Component({
  selector: 'app-bus',
  imports: [
    MatSlideToggle
  ],
  templateUrl: './bus.component.html',
  styleUrl: './bus.component.css'
})
export class BusComponent implements OnInit {
  private busService = inject(BusService);
  private busSubscription = inject(BusSubscription);
  private deviceSubscription = inject(DeviceSubscription);

  isConnected: boolean = false;
  railVoltageEnabled = false;

  ngOnInit() {
    this.busSubscription.railvoltage().subscribe(event => {
      this.railVoltageEnabled = event.state ? event.state : false;
    });
    this.deviceSubscription.deviceConnection().subscribe(device => {
      this.isConnected = device.eventType === TYPE.Connected;
    });
  }

  onRailVoltageToggle() {
    this.busService.railVoltage();
  }

  onRecordingToggle() {
    // TODO
    console.log("TODO: no recording");
  }
}
