import {Straight} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";

export class StraightBuilder extends AbstractTrackComponentBuilder {

  doBuild(trackPart: Straight, baseX: number, baseY: number): Element {
    let groupTransform = this.transformByDirection(trackPart.direction, baseX, baseY, AbstractTrackComponentBuilder.BASE_HEIGHT);
    return this.baseRect(baseX, baseY, AbstractTrackComponentBuilder.TILE, AbstractTrackComponentBuilder.BASE_HEIGHT, 'gray', groupTransform);
  }

  private transformByDirection(direction: string | undefined, baseX: number, baseY: number, height: number) {
    if (direction === 'VERTICAL') {
      const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
      const cy = baseY + height / 2;
      return `rotate(90 ${cx} ${cy})`;
    }
    return null;
  }
}
