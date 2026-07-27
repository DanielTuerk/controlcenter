import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {merge, of, switchMap} from 'rxjs';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
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
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {Station} from "../../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {StationService} from "../../../shared/station.service";
import {StationSubscription} from "../../../shared/websocket/station.subscription";

@Component({
  selector: 'app-scenario-station',
  imports: [
    MatButton,
    MatIconButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatTable,
    RouterLink,
    MatHeaderCellDef
  ],
  templateUrl: './station.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './station.component.css'
})
export class StationComponent {
  private stationService = inject(StationService);
  private stationSubscription = inject(StationSubscription);
  private dialog = inject(MatDialog);

  stations = toSignal(
    merge(of(null), this.stationSubscription.stationDataChanged()).pipe(
      switchMap(() => this.stationService.loadStations())
    ),
    {initialValue: []}
  );

  displayedColumns: string[] = ['id', 'name', 'action'];

  deleteStation(station: Station) {
    this.dialog.open(ConfirmDialogComponent, {data: station.name})
      .afterClosed().subscribe(result => {
      if (result === true) this.stationService.deleteStation(station.id!);
    });
  }
}
