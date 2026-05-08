import {Component, inject, OnInit} from '@angular/core';
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {BusSubscription} from "../../../shared/websocket/bus.subscription";
import {BusService} from "../../../shared/bus.service";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {BusDataDto, SYSTEMFORMAT, TYPE} from "../../../../shared/openapi-gen";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {BusSendDataDialogComponent} from "./bus-send-data-dialog/bus-send-data-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-bus',
  imports: [
    MatSlideToggle,
    MatButton,
    MatIcon,
    RouterLink,
    MatIconButton
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
  systemFormat = SYSTEMFORMAT.Unknown;

  constructor(private dialog: MatDialog) {
  }

  ngOnInit() {
    this.busSubscription.systemFormat().subscribe(event => {
      this.systemFormat = event.systemFormat ? event.systemFormat : SYSTEMFORMAT.Unknown;
    });
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

  onSystemFormat() {
    this.busService.busFormat();
  }

  protected sendBusData() {
    const dialogRef = this.dialog.open(BusSendDataDialogComponent, {
      data: {bus: 0, address: 1, value: 0} as BusDataDto
    });
    dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.busService.sendBusData(result);
        }
      }
    );
  }
}
