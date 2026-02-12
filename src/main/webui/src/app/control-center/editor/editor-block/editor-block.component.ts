import {Component, inject, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
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
import {TrackBlock} from "../../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {TrackService} from "../../../shared/track.service";
import {TrackSubscription} from "../../../shared/websocket/track.subscription";
import {ConstructionSubscription} from "../../../shared/websocket/construction.subscription";

@Component({
  selector: 'app-editor-block',
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
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
  templateUrl: './editor-block.component.html',
  styleUrl: './editor-block.component.css'
})
export class EditorBlockComponent implements OnInit {

  trackBlocks = signal<TrackBlock[]>([]);
  displayedColumns: string[] = ['id', 'name', 'address', 'bit', 'action'];
  readonly dialog = inject(MatDialog);
  private trackService = inject(TrackService);
  private trackSubscription = inject(TrackSubscription);
  private constructionSubscription = inject(ConstructionSubscription);

  ngOnInit() {
    this.constructionSubscription.currentConstruction().subscribe(event => {
      this.loadTrackBlocks();
    })

    this.trackSubscription.trackBlockDataChangedEvent().subscribe(event => {
      this.loadTrackBlocks();
    });
  }

  private loadTrackBlocks() {
    this.trackService.loadTrackBlocks().subscribe(data => {
      this.trackBlocks.set(data);
    })
  }

  deleteBlock(trackBlock: TrackBlock) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: trackBlock.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.trackService.deleteTrackBlock(trackBlock.id!);
      }
    });
  }

}
