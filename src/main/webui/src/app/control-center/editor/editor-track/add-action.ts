import {Injectable} from "@angular/core";
import {DIRECTION, DIRECTION1, DIRECTION2, PRESENTATION, TYPE3} from "../../../../shared/openapi-gen";
import {CurveBuilder} from "../../track/track-viewer-svg/track-builder/component/curve";
import {TurnoutBuilder} from "../../track/track-viewer-svg/track-builder/component/turnout";
import {SignalBuilder} from "../../track/track-viewer-svg/track-builder/component/signal";
import {StraightBuilder} from "../../track/track-viewer-svg/track-builder/component/straight";
import {BlockStraightBuilder} from "../../track/track-viewer-svg/track-builder/component/block-straight";
import {TrackElement} from "../../track/track-viewer-svg/track-element";
import {UncouplerBuilder} from "../../track/track-viewer-svg/track-builder/component/uncoupler";

@Injectable({providedIn: 'root'})
export class AddAction {

  private curveBuilder = new CurveBuilder();
  private turnoutBuilder = new TurnoutBuilder();
  private signalBuilder = new SignalBuilder();
  private straightBuilder = new StraightBuilder();
  private blockStraightBuilder = new BlockStraightBuilder();
  private uncouplerBuilder = new UncouplerBuilder();

  buildPaletteSvg(): TrackElement<any>[] {
    return [
      this.buildCurve(DIRECTION1.BottomRight, 0, 0),
      this.buildCurve(DIRECTION1.BottomLeft, 2, 0),
      this.buildCurve(DIRECTION1.TopRight, 4, 0),
      this.buildCurve(DIRECTION1.TopLeft, 6, 0),

      this.buildTurnout(DIRECTION2.Left, PRESENTATION.LeftToRight, 0, 2),
      this.buildTurnout(DIRECTION2.Left, PRESENTATION.BottomToTop, 2, 2),
      this.buildTurnout(DIRECTION2.Left, PRESENTATION.RightToLeft, 4, 2),
      this.buildTurnout(DIRECTION2.Left, PRESENTATION.TopToBottom, 6, 2),
      this.buildTurnout(DIRECTION2.Right, PRESENTATION.LeftToRight, 8, 2),
      this.buildTurnout(DIRECTION2.Right, PRESENTATION.BottomToTop, 10, 2),
      this.buildTurnout(DIRECTION2.Right, PRESENTATION.RightToLeft, 12, 2),
      this.buildTurnout(DIRECTION2.Right, PRESENTATION.TopToBottom, 14, 2),

      this.buildSignal(DIRECTION.Horizontal, 0, 4),
      this.buildSignal(DIRECTION.Vertical, 2, 4),

      this.buildStraight(DIRECTION.Horizontal, 4, 4),
      this.buildStraight(DIRECTION.Vertical, 6, 4),

      this.buildBlockStraight(DIRECTION.Horizontal, 8, 4),
      this.buildBlockStraight(DIRECTION.Vertical, 10, 4),

      this.buildUncoupler(DIRECTION.Horizontal, 12, 4),
      this.buildUncoupler(DIRECTION.Vertical, 14, 4),
    ];
  }

  private buildCurve(direction: DIRECTION1, x: number, y: number): TrackElement<any> {
    let trackPart = {
      trackPartType: 'Curve',
      gridPosition: {x: x, y: y},
      direction: direction
    };
    return {
      trackPart: trackPart,
      svgElement: this.curveBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

  private buildSignal(direction: DIRECTION, x: number, y: number) {
    let trackPart = {
      trackPartType: 'Signal',
      type: TYPE3.Block,
      gridPosition: {x: x, y: y},
      direction: direction
    };
    return {
      trackPart: trackPart,
      svgElement: this.signalBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

  private buildStraight(direction: DIRECTION, x: number, y: number) {
    let trackPart = {
      trackPartType: 'Straight',
      gridPosition: {x: x, y: y},
      direction: direction
    };
    return {
      trackPart: trackPart,
      svgElement: this.straightBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

  private buildBlockStraight(direction: DIRECTION, x: number, y: number) {
    let trackPart = {
      trackPartType: 'BlockStraight',
      gridPosition: {x: x, y: y},
      direction: direction
    };
    return {
      trackPart: trackPart,
      svgElement: this.blockStraightBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

  private buildTurnout(direction: DIRECTION2, presentation: PRESENTATION, x: number, y: number) {
    let trackPart = {
      trackPartType: 'Turnout',
      gridPosition: {x: x, y: y},
      currentDirection: direction,
      currentPresentation: presentation
    };
    return {
      trackPart: trackPart,
      svgElement: this.turnoutBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

  private buildUncoupler(direction: DIRECTION, x: number, y: number) {
    let trackPart = {
      trackPartType: 'Uncoupler',
      gridPosition: {x: x, y: y},
      direction: direction
    };
    return {
      trackPart: trackPart,
      svgElement: this.uncouplerBuilder.build(trackPart, null),
      lastEvent: null
    };
  }

}
