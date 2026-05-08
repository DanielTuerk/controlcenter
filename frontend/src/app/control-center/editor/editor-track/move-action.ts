import {Injectable} from "@angular/core";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {AbstractTrackPart, GridPosition} from "../../../../shared/openapi-gen";
import {
  AbstractTrackComponentBuilder
} from "../../track/track-viewer-svg/track-builder/abstract-track-component-builder";

class Movement {
  trackElementToMove: TrackElement<AbstractTrackPart>;
  pending: Point | null = null;
  last: Point | null = null;

  constructor(trackElementToMove: TrackElement<AbstractTrackPart>) {
    this.trackElementToMove = trackElementToMove;
  }
}

class Point {
  x: number;
  y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }
}

@Injectable({providedIn: 'root'})
export class MoveAction {

  private _unsavedMoveActions: Map<AbstractTrackPart, GridPosition> = new Map();

  private lastUpdate = 0;
  private movement: Movement | null = null;
  private rafId: number | null = null;

  isActive() {
    return this.movement != null;
  }

  get unsavedMoveActions(): Map<AbstractTrackPart, GridPosition> {
    return this._unsavedMoveActions;
  }

  startMovement($event: TrackElement<any>) {
    console.log(`move start trackpart x:${$event.trackPart.gridPosition?.x}
    y:${$event.trackPart.gridPosition?.y}`, $event);
    this.movement = new Movement($event);

    // TODO nur den container
    $event.svgElement.setAttribute('stroke', 'red');
    $event.svgElement.setAttribute('stroke-width', '4');

    // parse last movements of trackpart in the session, because not already updated grid position
    let existingTransform = $event.svgElement.getAttribute('transform');
    if (existingTransform && existingTransform?.startsWith('translate')) {
      let coordinates = existingTransform.substring('translate'.length + 1, existingTransform.length - 1)
      .split(',');
      this.movement.last = new Point(parseInt(coordinates[0].trim()), parseInt(coordinates[1].trim()));
      console.log(`start last: ${this.movement}`);
    }
  }

  stopMovement() {
    if (this.movement) {
      console.log('stop movement');
      this.movement.trackElementToMove!.svgElement.removeAttribute('stroke');
      this.movement.trackElementToMove!.svgElement.removeAttribute('stroke-width');
      this.movement = null;
    }
  }

  mouseOverTrackViewer($event: MouseEvent, offsetTop: number, offsetLeft: number) {
    if (this.movement != null) {
      const now = performance.now();
      if (now - this.lastUpdate < 16) { // ca. 60 FPS
        return;
      }
      this.lastUpdate = now;

      this.movement.pending = new Point($event.clientX - offsetLeft, $event.clientY - offsetTop);

      if (!this.rafId) {
        this.rafId = requestAnimationFrame(() => this.updatePosition());
      }
    }
  }

  private updatePosition() {
    if (this.movement && this.movement.pending !== null) {
      const tile = AbstractTrackComponentBuilder.TILE;
      const gridX = this.movement.trackElementToMove.trackPart.gridPosition?.x ?? 0;
      const gridY = this.movement.trackElementToMove.trackPart.gridPosition?.y ?? 0;

      const lastMovementX = tile * Math.floor((
        ((this.movement.last ? this.movement.last.x : 0) + this.movement.pending.x) - gridX * tile) / tile);
      const lastMovementY = tile * Math.floor((
        ((this.movement.last ? this.movement.last.y : 0) + this.movement.pending.y) - gridY * tile) / tile);

      this.movement.trackElementToMove.svgElement.setAttribute(
        'transform',
        `translate(${lastMovementX}, ${lastMovementY})`
      );
    }
    this.rafId = null;
  }

  onTrackViewerClicked() {
    if (this.movement != null
      && (this.movement.pending != null)) {
      let newX = Math.floor(this.movement.pending.x! / AbstractTrackComponentBuilder.TILE);
      let newY = Math.floor(this.movement.pending.y! / AbstractTrackComponentBuilder.TILE);

      let trackElementToMove = this.movement.trackElementToMove;
      trackElementToMove!.trackPart.gridPosition!.x = newX;
      trackElementToMove!.trackPart.gridPosition!.y = newY;

      this._unsavedMoveActions.set(trackElementToMove!.trackPart,
        {
          x: newX,
          y: newY
        }
      );

      console.log(`drop trackpart x:${newX} y:${newY}`, trackElementToMove);

      this.stopMovement();
    }
  }
}
