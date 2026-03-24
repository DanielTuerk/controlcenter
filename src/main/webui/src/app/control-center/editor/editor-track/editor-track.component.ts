import {Component, ElementRef, inject, OnDestroy, Renderer2, ViewChild} from '@angular/core';
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
import {MatExpansionPanel} from "@angular/material/expansion";
import {AddAction} from "./add-action";
import {Add, Move, Rotate} from "../../../../shared/openapi-gen";

export enum EDIT_TYPE {NONE, ADD, MOVE, ROTATE, DELETE, EDIT}

@Component({
  selector: 'app-editor-track',
  imports: [
    TrackViewerSvgComponent,
    MatToolbar,
    MatIcon,
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule,
    MatMiniFabButton,
    MatExpansionPanel
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
  private addAction = inject(AddAction);

  @ViewChild('trackViewer') trackViewerEl!: TrackViewerSvgComponent;
  @ViewChild('addPanel') addPanel!: MatExpansionPanel;
  @ViewChild('paletteSvg') paletteSvg!: ElementRef<SVGElement>;

  protected editType: EDIT_TYPE = EDIT_TYPE.NONE;

  protected onEditTypeChanged() {
    this.changeCursor();

    if (this.editType === EDIT_TYPE.ADD) {
      this.initAddAction();
    }
  }

  private initAddAction() {
    let tempId = -1;
    this.addAction.buildPaletteSvg().forEach(
      elem => {
        this.paletteSvg.nativeElement.appendChild(elem.svgElement);

        elem.svgElement.addEventListener("click", () => {
          const deepCopy = JSON.parse(JSON.stringify(elem));
          deepCopy.trackPart.id = tempId--;
          deepCopy.trackPart.gridPosition = {x: 0, y: 0};
          deepCopy.svgElement = this.trackViewerEl.addNewTrackPart(deepCopy);
          this.moveAction.stopMovement();
          this.moveAction.startMovement(deepCopy);
        });
      }
    );
    this.addPanel.expanded = this.editType === EDIT_TYPE.ADD;
  }

  protected onTrackPartClicked($event: TrackElement<any>) {
    if (!this.editType) {
      return;
    }
    if (this.moveAction.isActive()) {
      return;
    }
    switch (this.editType) {
      case EDIT_TYPE.ADD:
        // do nothing; handled by addPanel
        break;
      case EDIT_TYPE.ROTATE:
        this.trackViewerEl.repaintTrackPart(this.rotateAction.rotate($event));
        break;
      case EDIT_TYPE.MOVE:
        this.moveAction.startMovement($event);
        break;
      case EDIT_TYPE.DELETE:
        this.deleteAction.deleteTrackPart($event);
        break;
      case EDIT_TYPE.EDIT:
        this.editAction.editTrackPart($event);
        break;
      default:
        throw Error(`invalid edit type: ${this.editType}`);
    }
  }

  private changeCursor() {
    switch (this.editType) {
      case EDIT_TYPE.ROTATE:
        return this.changeCursorStyle('pointer');
      case EDIT_TYPE.EDIT:
        return this.changeCursorStyle('pointer');
      case EDIT_TYPE.MOVE:
        return this.changeCursorStyle('move');
      case EDIT_TYPE.DELETE:
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
      addActions: this.addChanges(),
      moveActions: this.moveChanges(),
      rotateActions: this.rotateChanges(),
    }).subscribe(() => {
      this.moveAction.unsavedMoveActions.clear();
      this.rotateAction.unsavedRotateChanges.clear();
    });
  }

  private addChanges() {
    let changes: Add[] = [];

    for (const [trackPart] of this.rotateAction.unsavedRotateChanges) {
      if (trackPart.id && trackPart.id < 0) {
        changes.push({
          trackPart: trackPart
        });
      }
    }

    for (const [trackPart] of this.moveAction.unsavedMoveActions) {
      if (trackPart.id && trackPart.id < 0) {
        // only add if not existing yet from other action
        if (changes.length == 0 || changes.find(x => x.trackPart.id != trackPart.id)) {
          changes.push({
            trackPart: trackPart
          });
        }
      }
    }
    return changes;
  }

  private rotateChanges() {
    let rotateActions: Rotate[] = [];
    if (this.rotateAction.unsavedRotateChanges.size > 0) {
      this.rotateAction.unsavedRotateChanges.forEach((value, trackPart) => {
        if (trackPart.id! > 0) {
          rotateActions.push({trackPartId: trackPart.id!, value: value});
        }
      });
    }
    return rotateActions;
  }

  private moveChanges() {
    let changes: Move[] = [];
    for (const [trackPart, gridPosition] of this.moveAction.unsavedMoveActions) {
      if (trackPart.id && trackPart.id > 0) {
        changes.push({
          trackPartId: trackPart.id!,
          gridPosition: gridPosition
        });
      }
    }
    return changes;
  }

  protected revert() {
    this.moveAction.stopMovement();

    this.rotateAction.unsavedRotateChanges.clear();
    this.moveAction.unsavedMoveActions.clear();

    this.trackViewerEl.reloadTrack();
  }

  protected mouseOverTrackViewer($event: MouseEvent) {
    this.moveAction.mouseOverTrackViewer($event,
      this.trackViewerEl.container.nativeElement.getBoundingClientRect().top,
      this.trackViewerEl.container.nativeElement.getBoundingClientRect().left);
  }

  protected onTrackViewerClicked() {
    this.moveAction.onTrackViewerClicked();
  }

  protected pendingChanges() {
    return this.moveAction.unsavedMoveActions.size > 0
      || this.rotateAction.unsavedRotateChanges.size > 0;
  }

  protected readonly EDIT_TYPE = EDIT_TYPE;
}
