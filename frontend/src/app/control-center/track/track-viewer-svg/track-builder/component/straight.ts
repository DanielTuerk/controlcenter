import {Straight, TrackPartStateEvent} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";

export class StraightBuilder<T extends Straight> extends AbstractTrackComponentBuilder<T, TrackPartStateEvent, TrackPartStateEvent> {

  override update(trackPart: Straight, event: TrackPartStateEvent): void {
    throw new Error("Method not implemented.");
  }

  doBuild(trackPart: Straight, baseX: number, baseY: number, event: TrackPartStateEvent | null = null): Element {
    let groupTransform = this.transformByDirection(trackPart.direction, baseX, baseY, AbstractTrackComponentBuilder.BASE_HEIGHT);
    return this.baseRect(baseX, baseY, AbstractTrackComponentBuilder.TILE, AbstractTrackComponentBuilder.BASE_HEIGHT, StraightBuilder.BASE_COLOR, groupTransform);
  }

  private transformByDirection(direction: string | undefined, baseX: number, baseY: number, height: number) {
    if (direction === 'VERTICAL') {
      const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
      const cy = baseY + height / 2;
      return `rotate( 90 ${cx} ${cy})`;
    }
    return null;
  }
}
