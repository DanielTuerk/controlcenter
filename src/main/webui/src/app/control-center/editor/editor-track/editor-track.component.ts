import {Component, inject, OnDestroy, Renderer2, ViewChild} from '@angular/core';
import {TrackViewerSvgComponent} from "../../track/track-viewer-svg/track-viewer-svg.component";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {TrackService} from "../../../shared/track.service";
import {MatMiniFabButton} from "@angular/material/button";
import {EditAction} from "./edit-action";
import {DeleteAction} from "./delete-action";
import {MoveAction} from "./move-action";
import {RotateAction} from "./rotate-action";

@Component({
  selector: 'app-editor-track',
  imports: [
    TrackViewerSvgComponent,
    MatToolbar,
    MatIcon,
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule,
    MatMiniFabButton
  ],
  templateUrl: './editor-track.component.html',
  styleUrl: './editor-track.component.css'
})
export class EditorTrackComponent implements OnDestroy {
  private renderer = inject(Renderer2);
  private trackService = inject(TrackService);
  private editAction = inject(EditAction);
  private deleteAction = inject(DeleteAction);
  private moveAction = inject(MoveAction);
  private rotateAction = inject(RotateAction);

  @ViewChild('trackViewer') trackViewerEl!: TrackViewerSvgComponent;


  protected editType: any;


  protected onEditTypeChanged() {
    this.changeCursor();
  }

  protected onTrackPartClicked($event: TrackElement<any>) {
    if (!this.editType) {
      return;
    }
    if (this.moveAction.isActive()) {
      return;
    }
    switch (this.editType) {
      case 'rotate':
        this.trackViewerEl.repaintTrackPart(this.rotateAction.rotate($event));
        break;
      case 'move':
        this.moveAction.startMovement($event);
        break;
      case 'delete':
        this.deleteAction.deleteTrackPart($event);
        break;
      case 'edit':
        this.editAction.editTrackPart($event);
        break;
      default:
        throw Error(`invalid edit type: ${this.editType}`);
    }
  }

  private changeCursor() {
    switch (this.editType) {
      case 'rotate':
        return this.changeCursorStyle('pointer');
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
    this.renderer.removeStyle(this.trackViewerEl.container.nativeElement, 'default');
  }

  private changeCursorStyle(value: string) {
    this.renderer.setStyle(this.trackViewerEl.container.nativeElement, 'cursor', value);
  }

  protected save() {
    this.moveAction.stopMovement();

    this.trackService.changeTrack({
      addActions: [],
      moveActions: this.moveChanges(),
      rotateActions: this.rotateChanges(),
    }).subscribe();
  }

  private rotateChanges() {
    let rotateActions: object[] = [];
    if (this.rotateAction.unsavedRotateChanges.size > 0) {
      this.rotateAction.unsavedRotateChanges.forEach((value, trackPartId) => {
        rotateActions.push({trackPartId: trackPartId, value: value});
      });
      this.rotateAction.unsavedRotateChanges.clear();
    }
    return rotateActions;
  }

  private moveChanges() {
    let moveActions: object[] = [];
    if (this.moveAction.unsavedMoveActions.size > 0) {
      this.moveAction.unsavedMoveActions.forEach((gridPos, trackPartId) => {
        moveActions.push({trackPartId: trackPartId, gridPosition: gridPos});
      });
      this.moveAction.unsavedMoveActions.clear();
    }
    return moveActions;
  }

  protected revert() {
    this.moveAction.stopMovement();

    this.rotateAction.unsavedRotateChanges.clear();
    this.moveAction.unsavedMoveActions.clear();

    this.trackViewerEl.reloadTrack();
  }

  protected mouseOverTrackViewer($event: MouseEvent) {
    this.moveAction.mouseOverTrackViewer($event,
      this.trackViewerEl.container.nativeElement.offsetTop,
      this.trackViewerEl.container.nativeElement.offsetLeft);
  }

  protected onTrackViewerClicked() {
    this.moveAction.onTrackViewerClicked();
  }

  protected pendingChanges() {
    return this.moveAction.unsavedMoveActions.size > 0
      || this.rotateAction.unsavedRotateChanges.size > 0;
  }
}
