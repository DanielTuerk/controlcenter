import {inject, Injectable, Type} from "@angular/core";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {AbstractTrackPart, BlockStraight, Signal, Turnout, Uncoupler} from "../../../../shared/openapi-gen";
import {SignalEditComponent} from "./edit/signal/signal-edit.component";
import {TrackBlockEditComponent} from "./edit/track-block/track-block-edit.component";
import {ToggleFunctionEditComponent} from "./edit/turnout/toggle-function-edit.component";
import {BaseEditTrackPartComponent} from "./edit/base-edit-track-part.component";
import {MatDialog} from "@angular/material/dialog";
import {TrackService} from "../../../shared/track.service";

@Injectable({providedIn: 'root'})
export class EditAction {

  private trackService = inject(TrackService);
  private readonly editDialog = inject(MatDialog);

  editTrackPart($event: TrackElement<any>) {
    this.trackService.loadTrackPart($event.trackPart.id!).subscribe(trackPart => {
      switch (trackPart.trackPartType) {
        case 'Signal':
          this.openEditDialog(trackPart as Signal, SignalEditComponent);
          break;
        case 'BlockStraight':
          this.openEditDialog(trackPart as BlockStraight, TrackBlockEditComponent);
          break;
        case 'Turnout':
          this.openEditDialog(trackPart as Turnout, ToggleFunctionEditComponent);
          break;
        case 'Uncoupler':
          this.openEditDialog(trackPart as Uncoupler, ToggleFunctionEditComponent);
          break;
      }
    });
  }

  private openEditDialog<T extends AbstractTrackPart>(trackPart: T, compType: Type<any>) {
    return this.editDialog.open(BaseEditTrackPartComponent, {
      minWidth: '600px',
      data: {
        componentType: compType,
        trackPart: trackPart
      }
    }).afterClosed();
  }
}
