import {Component, inject, OnDestroy, Renderer2, ViewChild} from '@angular/core';
import {TrackViewerSvgComponent} from "../../track/track-viewer-svg/track-viewer-svg.component";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {TrackService} from "../../../shared/track.service";
import {MatMiniFabButton} from "@angular/material/button";
import {Observable} from "rxjs";
import {EditAction} from "./edit-action";
import {DeleteAction} from "./delete-action";
import {MoveAction} from "./move-action";

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

  @ViewChild('trackViewer') trackViewerEl!: TrackViewerSvgComponent;


  protected editType: any;


  protected onEditTypeChanged() {
    this.changeCursor();
  }

  protected onTrackPartClicked($event: TrackElement<any>) {
    if (this.moveAction.isActive()) {
      return;
    }

    switch (this.editType) {
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
    // this.trackPartToMove = null;
    this.moveAction.stopMovement();

    if (this.moveAction.unsavedMoveActions.size > 0) {
      let moveActions: Observable<any>[] = [];
      this.moveAction.unsavedMoveActions.forEach((gridPos, trackPartId) => {
        moveActions.push(this.trackService.moveTrackPart(trackPartId, gridPos));
      });
      this.moveAction.unsavedMoveActions.clear();
      moveActions.forEach(x => x.subscribe());
    }
  }

  protected revert() {
    this.moveAction.stopMovement();

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
    return this.moveAction.unsavedMoveActions.size > 0;
  }
}
