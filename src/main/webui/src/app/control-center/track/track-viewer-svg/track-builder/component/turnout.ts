import {DIRECTION2, PRESENTATION, Turnout} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";

export class TurnoutBuilder extends AbstractTrackComponentBuilder {

  doBuild(trackPart: Turnout, baseX: number, baseY: number): Element {
    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;
    let degree = 0;
    let isLeft = trackPart.currentDirection === DIRECTION2.Left;
    switch (trackPart.currentPresentation) {
      case PRESENTATION.LeftToRight:
        degree = isLeft ? 180 : 0;
        break;
      case PRESENTATION.RightToLeft:
        degree = isLeft ? 0 : 180;
        break;
      case PRESENTATION.BottomToTop:
        degree = isLeft ? 270 : 90;
        break;
      case PRESENTATION.TopToBottom:
        degree = isLeft ? 90 : 270;
        break;
    }
    let path;
    if (isLeft) {
      path = `M ${baseX} ${cy} L ${baseX + AbstractTrackComponentBuilder.TILE - 5} ${cy} L ${baseX + 10} ${baseY + AbstractTrackComponentBuilder.TILE - 5}`
    } else {
      path = `M ${baseX + (AbstractTrackComponentBuilder.TILE)} ${cy} L ${baseX + 5} ${cy} L ${cx + 2} ${baseY + AbstractTrackComponentBuilder.TILE - 5}`;
    }

    const rect = this.createElement('path');
    rect.setAttribute('d', path);
    rect.setAttribute('width', `${AbstractTrackComponentBuilder.TILE}`);
    rect.setAttribute('height', `${AbstractTrackComponentBuilder.BASE_HEIGHT}`);
    rect.setAttribute('stroke', 'white');
    rect.setAttribute('stroke-width', `${AbstractTrackComponentBuilder.BASE_HEIGHT}`);
    rect.setAttribute('color', 'white');
    rect.setAttribute('fill', 'none');
    rect.setAttribute('transform', `rotate(${degree} ${cx} ${cy})`);
    return rect;
  }


}
