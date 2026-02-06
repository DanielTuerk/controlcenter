import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {Signal, TrackPartStateEvent} from "../../../../../../shared/openapi-gen";

export class SignalBuilder extends AbstractTrackComponentBuilder {

  doBuild(trackPart: Signal, baseX: number, baseY: number, event: TrackPartStateEvent | null = null): Element {
    return this.baseRect(baseX, baseY - AbstractTrackComponentBuilder.BASE_HEIGHT, AbstractTrackComponentBuilder.TILE, 21, 'red', null);
  }

}
