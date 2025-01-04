import {Component, inject, OnInit} from '@angular/core';
import {TrainService} from "../../shared/train.service";
import {RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {Train} from "../../../shared/gen-js/train_types";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../common/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-train',
  imports: [
    RouterLink,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatTableModule,
    MatIcon
  ],
  templateUrl: './train.component.html',
  styleUrl: './train.component.css'
})
export class TrainComponent implements OnInit {
  private trainService = inject(TrainService);
  trains = this.trainService.loadedTrains;
  displayedColumns: string[] = ['id', 'name', 'address', 'action'];
  readonly dialog = inject(MatDialog);

  ngOnInit() {
    this.trainService.loadTrains()
  }

  deleteTrain(train: Train) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: train.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.trainService.deleteTrain(train.id);
      }
    });
  }
}
