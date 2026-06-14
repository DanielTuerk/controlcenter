import {BlockStraight, DIRECTION, TrackPartBlockEvent, TrackPartStateEvent} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {TrainService} from "../../../../../shared/train.service";

export class BlockStraightBuilder extends AbstractTrackComponentBuilder<BlockStraight, TrackPartStateEvent> {
  private trainService: TrainService;

  constructor(trainService: TrainService) {
    super();
    this.trainService = trainService;
  }


  doBuild(trackPart: BlockStraight, baseX: number, baseY: number, event: TrackPartBlockEvent | null = null): Element {
    let blockSize = trackPart.blockLength !== undefined
      ? AbstractTrackComponentBuilder.TILE * trackPart.blockLength
      : AbstractTrackComponentBuilder.TILE;
    let blockThickness = AbstractTrackComponentBuilder.TILE - 4;

    const group = this.createElement('g');

    let trainDisplayValue = '';
    if (event && event.feedbackData && event.feedbackData.trainAddress) {
      let train = this.trainService.cachedTrainByAddress(event.feedbackData.trainAddress);
      trainDisplayValue = train ? train.name ?? `(${train.address})` : 'n.A.';
    }
    let blockColor = event && event.occupied ? BLOCK_COLOR_OCCUPIED : BLOCK_COLOR_FREE;
    let rect, text;
    if (trackPart.direction === DIRECTION.Vertical) {
      rect = this.baseRect(baseX + 2, baseY - 9, blockThickness, blockSize, blockColor, null);
      text = this.text(trainDisplayValue, baseX + 12, baseY - 8,
        `rotate(90 ${baseX + AbstractTrackComponentBuilder.TILE} ${baseY - 4})`);
    } else {
      rect = this.baseRect(baseX, baseY - 7.5, blockSize, blockThickness, blockColor, null);
      text = this.text(trainDisplayValue, baseX - 8, baseY - 12, null);
    }
    group.appendChild(rect);
    group.appendChild(text);

    requestAnimationFrame(() => {
      // do it after rendering to receive the calculated text length
      this.truncateSvgText(text as SVGTextElement, blockSize);
    });

    return group;
  }

  private truncateSvgText(
    textElement: SVGTextElement,
    maxWidthPx: number
  ): void {

    // passt schon komplett
    if (textElement.getComputedTextLength() <= maxWidthPx) {
      return;
    }

    let truncated = textElement.textContent ?? '';

    while (truncated.length > 0) {
      truncated = truncated.slice(0, -1);
      textElement.textContent = truncated;

      if (textElement.getComputedTextLength() <= maxWidthPx) {
        return;
      }
    }

    textElement.textContent = '';
  }

}

const BLOCK_COLOR_FREE = 'green';
const BLOCK_COLOR_OCCUPIED = 'red';
