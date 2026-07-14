import {
  BlockStraight,
  DIRECTION,
  DRIVINGDIRECTION1,
  TrackPartBlockEvent,
  TrackPartStateEvent,
  TrainInBlockEvent
} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {TrainService} from "../../../../../shared/train.service";

export class BlockStraightBuilder extends AbstractTrackComponentBuilder<BlockStraight, TrackPartStateEvent, TrainInBlockEvent> {
  private trainService: TrainService;

  private trainOnBlocks: string[] = [];

  constructor(trainService: TrainService) {
    super();
    this.trainService = trainService;
  }

  update(trackPart: BlockStraight, event: TrainInBlockEvent) {
    let train = this.trainService.cachedTrainByAddress(event.trainAddress!);
    let text = document.getElementById(`track-part-${trackPart.id}-feedback-text`) as SVGTextElement | null;
    if (text) {
      let direction = event.trainDirection === DRIVINGDIRECTION1.Backward ? '<- ' : '-> ';
      let content = `${direction} ` + (train ? train.name ?? `(${train.address})` : 'n.A.');
      text.textContent = content;
      requestAnimationFrame(() => {
        // do it after rendering to receive the calculated text length
        this.truncateSvgText(text as SVGTextElement, this.calcBlockSize(trackPart));
      });

      this.trainOnBlocks[trackPart!.id!] = content;
    }
  }

  doBuild(trackPart: BlockStraight, baseX: number, baseY: number, event: TrackPartBlockEvent | null = null): Element {
    let blockSize = this.calcBlockSize(trackPart);
    let blockThickness = AbstractTrackComponentBuilder.TILE - 4;

    const group = this.createElement('g');

    if (!event || !event.occupied) {
      this.trainOnBlocks[trackPart!.id!] = '';
    }

    let trainDisplayValue = this.trainOnBlocks[trackPart!.id!];

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
    text.id = `track-part-${trackPart.id}-feedback-text`;
    group.appendChild(rect);
    group.appendChild(text);

    requestAnimationFrame(() => {
      // do it after rendering to receive the calculated text length
      this.truncateSvgText(text as SVGTextElement, blockSize);
    });

    return group;
  }

  private calcBlockSize(trackPart: BlockStraight) {
    return trackPart.blockLength !== undefined
      ? AbstractTrackComponentBuilder.TILE * trackPart.blockLength
      : AbstractTrackComponentBuilder.TILE;
  }

  private truncateSvgText(
    textElement: SVGTextElement,
    maxWidthPx: number
  ): void {
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
