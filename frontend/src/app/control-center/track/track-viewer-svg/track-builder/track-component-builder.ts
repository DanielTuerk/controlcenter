import {TurnoutBuilder} from "./component/turnout";
import {CurveBuilder} from "./component/curve";
import {StraightBuilder} from "./component/straight";
import {BlockStraightBuilder} from "./component/block-straight";
import {SignalBuilder} from "./component/signal";
import {
  AbstractTrackPart,
  BlockStraight,
  Curve,
  Signal,
  Straight,
  TrackPartStateEvent,
  Turnout,
  Uncoupler
} from "../../../../../shared/openapi-gen";
import {Injectable} from "@angular/core";
import {UncouplerBuilder} from "./component/uncoupler";

@Injectable({providedIn: 'root'})
export class TrackComponentBuilder {
  turnoutBuilder = new TurnoutBuilder();
  signalBuilder = new SignalBuilder();
  straightBuilder = new StraightBuilder();
  blockStraightBuilder = new BlockStraightBuilder();
  curveBuilder = new CurveBuilder();
  uncouplerBuilder = new UncouplerBuilder();

  build(trackPart: AbstractTrackPart, event: TrackPartStateEvent | null = null) {
    if ("trackPartType" in trackPart) {
      switch (trackPart.trackPartType) {
        case 'Turnout':
          return this.turnoutBuilder.build(trackPart as Turnout, event);
        case 'Signal':
          return this.signalBuilder.build(trackPart as Signal, event);
        case 'Straight':
          return this.straightBuilder.build(trackPart as Straight, event);
        case 'BlockStraight':
          return this.blockStraightBuilder.build(trackPart as BlockStraight, event);
        case 'Curve':
          return this.curveBuilder.build(trackPart as Curve, event);
        case 'Uncoupler':
          return this.uncouplerBuilder.build(trackPart as Uncoupler, event);
        default:
          console.error("undefined trackPartType", trackPart);
          throw new Error("unknown trackPartType: " + trackPart.trackPartType);
      }
    }
    throw new Error("no trackPartType:" + trackPart);
  }
}
