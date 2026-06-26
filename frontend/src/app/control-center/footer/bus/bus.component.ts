import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {BusService} from "../../../shared/bus.service";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {BusDataDto} from "../../../../shared/openapi-gen";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {BusSendDataDialogComponent} from "./bus-send-data-dialog/bus-send-data-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatTooltip} from "@angular/material/tooltip";
import {BusSubscription} from "../../../shared/websocket/bus.subscription";

@Component({
  selector: 'app-bus',
  imports: [
    MatButton,
    MatIcon,
    RouterLink,
    MatIconButton,
    MatTooltip
  ],
  templateUrl: './bus.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './bus.component.css'
})
export class BusComponent {
  private busService = inject(BusService);
  private busSubscription = inject(BusSubscription);
  private deviceSubscription = inject(DeviceSubscription);

  readonly isConnected = this.deviceSubscription.isConnected;
  readonly railVoltageEnabled = this.busSubscription.railVoltage;
  readonly systemFormat = this.busSubscription.systemFormat;

  constructor(private dialog: MatDialog) {
  }

  onRailVoltageToggle() {
    this.busService.toggleRailVoltage();
  }

  onSystemFormat() {
    this.busService.switchSystemFormat();
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
