import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {merge, switchMap} from 'rxjs';
import {MatButton, MatIconButton} from "@angular/material/button";
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
    MatIconButton,
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
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './editor-block.component.css'
})
export class EditorBlockComponent {
  private trackService = inject(TrackService);
  private trackSubscription = inject(TrackSubscription);
  private constructionSubscription = inject(ConstructionSubscription);
  private dialog = inject(MatDialog);

  trackBlocks = toSignal(
    merge(
      this.constructionSubscription.currentConstruction(),
      this.trackSubscription.trackBlockDataChangedEvent()
    ).pipe(switchMap(() => this.trackService.loadTrackBlocks())),
    {initialValue: []}
  );

  displayedColumns: string[] = ['id', 'name', 'address', 'bit', 'action'];

  deleteBlock(trackBlock: TrackBlock) {
    this.dialog.open(ConfirmDialogComponent, {data: trackBlock.name})
      .afterClosed().subscribe(result => {
      if (result === true) this.trackService.deleteTrackBlock(trackBlock.id!);
    });
  }
}
