import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {merge, of, switchMap} from 'rxjs';
import {TrainService} from "../../shared/train.service";
import {RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../common/confirm-dialog/confirm-dialog.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {Train} from "../../../shared/openapi-gen";
import {TrainSubscription} from "../../shared/websocket/train.subscription";

@Component({
  selector: 'app-train',
  imports: [
    RouterLink,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatTableModule,
    MatIcon,
    MatButton,
    MatIconButton
  ],
  templateUrl: './train.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './train.component.css'
})
export class TrainComponent {
  private trainService = inject(TrainService);
  private trainSubscription = inject(TrainSubscription);
  private dialog = inject(MatDialog);

  trains = toSignal(
    merge(of(null), this.trainSubscription.trainDataChanged()).pipe(
      switchMap(() => this.trainService.loadTrains())
    ),
    {initialValue: []}
  );

  displayedColumns: string[] = ['id', 'name', 'address', 'action'];

  deleteTrain(train: Train) {
    this.dialog.open(ConfirmDialogComponent, {data: train.name})
      .afterClosed().subscribe(result => {
      if (result === true) this.trainService.deleteTrain(train.id!);
    });
  }
}
