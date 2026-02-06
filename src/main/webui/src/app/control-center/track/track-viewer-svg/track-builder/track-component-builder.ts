import {TurnoutBuilder} from "./component/turnout";
import {CurveBuilder} from "./component/curve";
import {StraightBuilder} from "./component/straight";
import {BlockStraightBuilder} from "./component/block-straight";
import {SignalBuilder} from "./component/signal";
import {AbstractTrackPart, TrackPartStateEvent} from "../../../../../shared/openapi-gen";
import {inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class TrackComponentBuilder {
  turnoutBuilder = inject(TurnoutBuilder);
  signalBuilder = inject(SignalBuilder);
  straightBuilder = inject(StraightBuilder);
  blockStraightBuilder = inject(BlockStraightBuilder);
  curveBuilder = inject(CurveBuilder);

  build(trackPart: AbstractTrackPart, event: TrackPartStateEvent | null = null) {
    switch (trackPart.trackPartType) {
      case 'Turnout':
        return this.turnoutBuilder.build(trackPart,event);
      case 'Signal':
        return this.signalBuilder.build(trackPart, event);
      case 'Straight':
        return this.straightBuilder.build(trackPart,event);
      case 'BlockStraight':
        return this.blockStraightBuilder.build(trackPart,event);
      case 'Curve':
        return this.curveBuilder.build(trackPart,event);
      default:
        console.error("undefined trackPartType", trackPart);
        throw new Error("unknown trackPartType: " + trackPart.trackPartType);
    }
  }
}
