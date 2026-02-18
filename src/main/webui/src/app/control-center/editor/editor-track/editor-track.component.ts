import {Component, ElementRef, inject, OnDestroy, Renderer2, Type, ViewChild} from '@angular/core';
import {TrackViewerSvgComponent} from "../../track/track-viewer-svg/track-viewer-svg.component";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {SignalEditComponent} from "./edit/signal/signal-edit.component";
import {TrackBlockEditComponent} from "./edit/track-block/track-block-edit.component";
import {ToggleFunctionEditComponent} from "./edit/turnout/toggle-function-edit.component";
import {MatDialog} from "@angular/material/dialog";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {TrackService} from "../../../shared/track.service";
import {BaseEditTrackPartComponent} from "./edit/base-edit-track-part.component";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {AbstractTrackPart, BlockStraight, Signal, Turnout, Uncoupler} from "../../../../shared/openapi-gen";

@Component({
  selector: 'app-editor-track',
  imports: [
    TrackViewerSvgComponent,
    MatToolbar,
    MatIcon,
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule
  ],
  templateUrl: './editor-track.component.html',
  styleUrl: './editor-track.component.css'
})
export class EditorTrackComponent implements OnDestroy {
  private renderer = inject(Renderer2);
  private trackService = inject(TrackService);

  @ViewChild('trackViewer', {read: ElementRef}) trackViewerEl!: ElementRef;

  private readonly editDialog = inject(MatDialog);
  protected editType: any;
  readonly deleteConfirmDialog = inject(MatDialog);

  protected onEditTypeChanged() {
    this.changeCursor();
  }

  protected onTrackPartClicked($event: TrackElement<any>) {
    switch (this.editType) {
      case 'move':
        // TODO
        alert("todo");
        break;
      case 'delete':
        this.deleteTrackPart($event);
        break;
      case 'edit':
        this.editTrackPart($event);
        break;
      default:
        throw Error(`invalid edit type: ${this.editType}`);
    }
  }

  private deleteTrackPart($event: TrackElement<any>) {
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

  private editTrackPart($event: TrackElement<any>) {
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

  private changeCursor() {
    switch (this.editType) {
      case 'edit':
        return this.changeCursorStyle('pointer');
      case 'move':
        return this.changeCursorStyle('move');
      case 'delete':
        return this.changeCursorStyle('grabbing');
      default:
        return this.changeCursorStyle('default');
    }
  }

  ngOnDestroy() {
    this.renderer.removeStyle(this.trackViewerEl.nativeElement, 'default');
  }

  private changeCursorStyle(value: string) {
    this.renderer.setStyle(this.trackViewerEl.nativeElement, 'cursor', value);
  }
}
