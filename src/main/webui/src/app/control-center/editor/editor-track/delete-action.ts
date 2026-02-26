import {inject, Injectable} from "@angular/core";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {MatDialog} from "@angular/material/dialog";
import {TrackService} from "../../../shared/track.service";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";

@Injectable({providedIn: 'root'})
export class DeleteAction {

  private trackService = inject(TrackService);
  readonly deleteConfirmDialog = inject(MatDialog);

  deleteTrackPart($event: TrackElement<any>) {
    this.deleteConfirmDialog.open(ConfirmDialogComponent, {
      data: $event.trackPart.id
    })
    .afterClosed()
    .subscribe(result => {
      if (result === true) {
        this.trackService.deleteTrackPart($event.trackPart.id!);
      }
    });
  }
}
