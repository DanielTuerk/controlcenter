import {BlockStraight, DIRECTION, TrackPartStateEvent} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";

export class BlockStraightBuilder extends AbstractTrackComponentBuilder<BlockStraight, TrackPartStateEvent> {

  doBuild(trackPart: BlockStraight, baseX: number, baseY: number, event: TrackPartStateEvent | null = null): Element {
    let blockSize = trackPart.blockLength !== undefined
      ? AbstractTrackComponentBuilder.TILE * trackPart.blockLength
      : AbstractTrackComponentBuilder.TILE;
    let blockThickness = AbstractTrackComponentBuilder.TILE - 4;

    if (trackPart.direction === DIRECTION.Vertical) {
      return this.baseRect(baseX + 2, baseY - 9, blockThickness, blockSize, 'green', null);
    } else {
      return this.baseRect(baseX, baseY - 7.5, blockSize, blockThickness, 'green', null);
    }
  }

}
