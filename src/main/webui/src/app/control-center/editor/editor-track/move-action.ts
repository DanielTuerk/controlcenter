import {Injectable} from "@angular/core";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {AbstractTrackPart, GridPosition} from "../../../../shared/openapi-gen";
import {
  AbstractTrackComponentBuilder
} from "../../track/track-viewer-svg/track-builder/abstract-track-component-builder";

@Injectable({providedIn: 'root'})
export class MoveAction {

  private _unsavedMoveActions: Map<number, GridPosition> = new Map();
  private trackPartToMove: TrackElement<AbstractTrackPart> | null = null;

  private lastUpdate = 0;
  private pendingX: number | null = null;
  private pendingY: number | null = null;
  private rafId: number | null = null;

  isActive() {
    return this.trackPartToMove != null;
  }

  get unsavedMoveActions(): Map<number, GridPosition> {
    return this._unsavedMoveActions;
  }

  startMovement($event: TrackElement<any>) {
    console.log("move start");

    // TODO nur den container
    $event.svgElement.setAttribute('stroke', 'red');
    $event.svgElement.setAttribute('stroke-width', '4');
    this.trackPartToMove = $event;
  }

  stopMovement() {
    if (this.trackPartToMove) {
      this.trackPartToMove!.svgElement.removeAttribute('stroke');
      this.trackPartToMove!.svgElement.removeAttribute('stroke-width');
    }
    this.trackPartToMove = null;
    this.pendingX = null;
    this.pendingY = null;
  }

  mouseOverTrackViewer($event: MouseEvent, offsetTop: number, offsetLeft: number) {
    if (this.trackPartToMove != null) {

      const now = performance.now();
      if (now - this.lastUpdate < 16) { // ca. 60 FPS
        return;
      }
      this.lastUpdate = now;

      this.pendingX = $event.clientX - offsetLeft
      this.pendingY = $event.clientY - offsetTop

      if (!this.rafId) {
        this.rafId = requestAnimationFrame(() => this.updatePosition());
      }
    }
  }

  private updatePosition() {
    if (this.trackPartToMove && this.pendingX !== null && this.pendingY !== null) {
      const tile = AbstractTrackComponentBuilder.TILE;
      const gridX = this.trackPartToMove.trackPart.gridPosition?.x ?? 0;
      const gridY = this.trackPartToMove.trackPart.gridPosition?.y ?? 0;

      const lastMovementX = tile * Math.floor((this.pendingX - gridX * tile) / tile);
      const lastMovementY = tile * Math.floor((this.pendingY - gridY * tile) / tile);

      this.trackPartToMove.svgElement.setAttribute(
        'transform',
        `translate(${lastMovementX}, ${lastMovementY})`
      );
    }
    this.rafId = null;
  }

  onTrackViewerClicked() {
    if (this.trackPartToMove != null
      && (this.pendingX != null || this.pendingY != null)) {

      console.log("drop trackpart", this.trackPartToMove);

      let newX = Math.floor(this.pendingX! / AbstractTrackComponentBuilder.TILE);
      let newY = Math.floor(this.pendingY! / AbstractTrackComponentBuilder.TILE);

      this.trackPartToMove!.trackPart.gridPosition!.x = newX;
      this.trackPartToMove!.trackPart.gridPosition!.y = newY;

      this._unsavedMoveActions.set(this.trackPartToMove!.trackPart.id!,
        {
          x: newX,
          y: newY
        }
      );

      this.stopMovement();
    }
  }
}
