import {TurnoutBuilder} from "./component/turnout";
import {CurveBuilder} from "./component/curve";
import {StraightBuilder} from "./component/straight";
import {BlockStraightBuilder} from "./component/block-straight";
import {SignalBuilder} from "./component/signal";
import {AbstractTrackPart} from "../../../../../shared/openapi-gen";
import {inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class TrackComponentBuilder {
  turnoutBuilder = inject(TurnoutBuilder);
  signalBuilder = inject(SignalBuilder);
  straightBuilder = inject(StraightBuilder);
  blockStraightBuilder = inject(BlockStraightBuilder);
  curveBuilder = inject(CurveBuilder);

  build(trackPart: AbstractTrackPart) {
    switch (trackPart.trackPartType) {
      case 'Turnout':
        return this.turnoutBuilder.build(trackPart);
      case 'Signal':
        return this.signalBuilder.build(trackPart);
      case 'Straight':
        return this.straightBuilder.build(trackPart);
      case 'BlockStraight':
        return this.blockStraightBuilder.build(trackPart);
      case 'Curve':
        return this.curveBuilder.build(trackPart);
      default:
        console.error("undefined trackPartType", trackPart);
        throw new Error("unknown trackPartType: " + trackPart.trackPartType);
    }
  }
}
