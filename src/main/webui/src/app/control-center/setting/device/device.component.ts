import {Component, inject, OnInit, signal} from '@angular/core';
import {DeviceInfo} from "../../../../shared/openapi-gen";
import {DeviceService} from "../../../shared/device.service";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-device',
  imports: [
    MatCardContent,
    MatCardTitle,
    MatCardHeader,
    MatCard,
    MatTable,
    MatButton,
    RouterLink,
    MatIcon,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    MatHeaderCell,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef
  ],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css'
})
export class DeviceComponent implements OnInit {

  private deviceService = inject(DeviceService);
  devices = signal<DeviceInfo[]>([]);
  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = ['id', 'key', 'type', 'action'];

  ngOnInit() {
    this.deviceService.loadDevices().subscribe(data => {
      this.devices.set(data);
    })
  }

  deleteDevice(device: DeviceInfo) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: device.key
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deviceService.deleteDevice(device.id!);
      }
    });
  }
}
