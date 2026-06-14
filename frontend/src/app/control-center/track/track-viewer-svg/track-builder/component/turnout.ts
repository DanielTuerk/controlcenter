import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {DIRECTION2, PRESENTATION, TrackPartStateEvent, Turnout} from "../../../../../../shared/openapi-gen";

export class TurnoutBuilder extends AbstractTrackComponentBuilder<Turnout, TrackPartStateEvent> {

  doBuild(trackPart: Turnout, baseX: number, baseY: number, event: TrackPartStateEvent | null = null): Element {
    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;

    const isLeft = trackPart.currentDirection === DIRECTION2.Left;

    const straightRectPoints = `
  ${baseX},${baseY}
  ${baseX + AbstractTrackComponentBuilder.TILE},${baseY}
  ${baseX + AbstractTrackComponentBuilder.TILE},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT}
  ${baseX},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT}`;

    const turnTrapPoints = isLeft ?
      `
   ${baseX + AbstractTrackComponentBuilder.TILE},${baseY}
  ${baseX + AbstractTrackComponentBuilder.BASE_HEIGHT},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT * 2}
  ${baseX + AbstractTrackComponentBuilder.TILE / 2 + AbstractTrackComponentBuilder.BASE_HEIGHT / 2}, ${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT * 2}
  ${baseX + AbstractTrackComponentBuilder.TILE},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT}
  ` : `
  ${baseX},${baseY}
  ${baseX + AbstractTrackComponentBuilder.TILE / 2 + AbstractTrackComponentBuilder.BASE_HEIGHT / 2}, ${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT * 2}
  ${baseX + AbstractTrackComponentBuilder.BASE_HEIGHT},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT * 2}
  ${baseX},${baseY + AbstractTrackComponentBuilder.BASE_HEIGHT}
  `;

    const group = document.createElementNS('http://www.w3.org/2000/svg', 'g');

    let transform = `rotate(${this.calcDegreeValue(trackPart, isLeft)} ${cx} ${cy})`;

    if (event !== null && event.state) {
      group.appendChild(this.basePolygon(straightRectPoints, 'grey', transform));
      group.appendChild(this.basePolygon(turnTrapPoints, 'white', transform));
    } else {
      group.appendChild(this.basePolygon(turnTrapPoints, 'grey', transform));
      group.appendChild(this.basePolygon(straightRectPoints, 'white', transform));
    }
    return group;
  }

  private calcDegreeValue(trackPart: Turnout, isLeft: boolean) {
    switch (trackPart.currentPresentation) {
      case PRESENTATION.LeftToRight:
        return isLeft ? 180 : 0;
      case PRESENTATION.RightToLeft:
        return isLeft ? 0 : 180;
      case PRESENTATION.BottomToTop:
        return isLeft ? 270 : 90;
      case PRESENTATION.TopToBottom:
        return isLeft ? 90 : 270;
      default:
        throw Error(`invalid presentation value: ${trackPart.currentPresentation}`);
    }
  }

  private basePolygon(
    points: string,
    fill: string,
    transform?: string,
    stroke: string = 'black',
    strokeWidth: string = '0'
  ): Element {
    const polygon = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
    polygon.setAttribute('points', points);
    polygon.setAttribute('fill', fill);
    polygon.setAttribute('stroke', stroke);
    polygon.setAttribute('stroke-width', strokeWidth);
    if (transform) {
      polygon.setAttribute('transform', transform);
    }
    return polygon;
  }

}
