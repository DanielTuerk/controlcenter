import {Curve} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";

export class CurveBuilder extends AbstractTrackComponentBuilder {

  doBuild(trackPart: Curve, baseX: number, baseY: number): Element {
    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;
    let degree;
    switch (trackPart.direction) {
      case 'BOTTOM_RIGHT':
        degree = 270;
        break;
      case 'BOTTOM_LEFT':
        degree = 0;
        break;
      case 'TOP_LEFT':
        degree = 90;
        break;
      case 'TOP_RIGHT':
        degree = 180;
        break;
    }

    const rect = this.createElement('path');
    rect.setAttribute('d', `M ${baseX} ${cy} L ${cx} ${cy} L ${cx} ${cy + AbstractTrackComponentBuilder.BASE_HEIGHT * 2 - 1}`);
    rect.setAttribute('stroke', 'white');
    rect.setAttribute('stroke-width', `${AbstractTrackComponentBuilder.BASE_HEIGHT}`);
    rect.setAttribute('fill', 'none');
    rect.setAttribute('transform', `rotate(${degree} ${cx} ${cy})`);
    return rect;
  }

}
