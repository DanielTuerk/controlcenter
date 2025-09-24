import {Component, inject, OnInit, signal} from '@angular/core';
import {TrainService} from "../../shared/train.service";
import {RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../common/confirm-dialog/confirm-dialog.component";
import {Train} from "../../../shared/openapi-gen";

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
  trains = signal<Train[]>([]);//this.trainService.loadedTrains;
  displayedColumns: string[] = ['id', 'name', 'address', 'action'];
  readonly dialog = inject(MatDialog);

  ngOnInit() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data);
    })
  }

  deleteTrain(train: Train) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: train.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.trainService.deleteTrain(train.id!);
      }
    });
  }
}
