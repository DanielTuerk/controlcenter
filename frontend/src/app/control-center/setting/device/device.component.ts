import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {map, merge, of, switchMap} from 'rxjs';
import {DeviceInfo} from "../../../../shared/openapi-gen";
import {DeviceService} from "../../../shared/device.service";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatButton, MatIconButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";

@Component({
  selector: 'app-device',
  imports: [
    MatCardContent,
    MatCardTitle,
    MatCardHeader,
    MatTable,
    MatButton,
    MatIconButton,
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
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './device.component.css'
})
export class DeviceComponent {
  private deviceService = inject(DeviceService);
  private deviceSubscription = inject(DeviceSubscription);
  private dialog = inject(MatDialog);

  devices = toSignal(
    merge(of(null), this.deviceSubscription.deviceDataChanged()).pipe(
      switchMap(() => this.deviceService.loadDevices())
    ),
    {initialValue: []}
  );

  isConnected = toSignal(
    this.deviceSubscription.deviceConnection().pipe(map(d => d.connected ?? false)),
    {initialValue: false}
  );

  displayedColumns: string[] = ['id', 'key', 'type', 'action'];

  deleteDevice(device: DeviceInfo) {
    this.dialog.open(ConfirmDialogComponent, {data: device.key})
      .afterClosed().subscribe(result => {
      if (result === true) this.deviceService.deleteDevice(device.id!);
    });
  }
}
