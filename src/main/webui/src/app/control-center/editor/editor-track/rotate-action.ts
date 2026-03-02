import {Injectable} from "@angular/core";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {
  AbstractTrackPart,
  Curve,
  DIRECTION,
  DIRECTION1,
  PRESENTATION,
  Straight,
  Turnout
} from "../../../../shared/openapi-gen";

@Injectable({providedIn: 'root'})
export class RotateAction {

  private _unsavedRotateChanges: Map<number, string> = new Map();

  get unsavedRotateChanges() {
    return this._unsavedRotateChanges;
  }

  rotate($event: TrackElement<any>) {
    let trackPart = $event.trackPart;
    let newValue = this.apply(trackPart);
    if (newValue) {
      this.unsavedRotateChanges.set(trackPart.id!, newValue);
    }
    return $event;
  }

  private apply(trackPart: AbstractTrackPart): string | null {
    switch (trackPart.trackPartType) {
      case 'Straight':
      case 'Signal':
      case 'Uncoupler':
      case 'BlockStraight':
        let straight = trackPart as Straight;
        straight.direction = this.nextValue(straight.direction, Object.values(DIRECTION));
        return straight.direction;
      case 'Curve':
        let curve = trackPart as Curve;
        curve.direction = this.nextValue(curve.direction, Object.values(DIRECTION1));
        return curve.direction;
      case 'Turnout':
        let turnout = trackPart as Turnout;
        turnout.currentPresentation = this.nextValue(turnout.currentPresentation, Object.values(PRESENTATION));
        return turnout.currentPresentation;
    }
    return null;
  }

  private nextValue<T>(current: any, values: T[]) {
    const idx = values.indexOf(current ?? values[0]);
    const nextIdx = (idx + 1) % values.length;
    return values[nextIdx] as T;
  }
}
