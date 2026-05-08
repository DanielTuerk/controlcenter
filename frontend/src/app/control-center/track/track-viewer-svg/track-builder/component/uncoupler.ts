import {TrackPartStateEvent, Uncoupler} from "../../../../../../shared/openapi-gen";
import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {StraightBuilder} from "./straight";

export class UncouplerBuilder extends StraightBuilder<Uncoupler> {

  override doBuild(trackPart: Uncoupler, baseX: number, baseY: number, event: TrackPartStateEvent | null = null): Element {
    const group = this.createElement('g');
    group.appendChild(super.doBuild(trackPart, baseX, baseY, event));

    const isOn = event !== null && event.on;
    const height = isOn ? AbstractTrackComponentBuilder.TILE / 1.5 : AbstractTrackComponentBuilder.TILE / 2;
    const width = AbstractTrackComponentBuilder.TILE / 2;
    const color = isOn ? 'blue' : 'gray';

    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;
    group.appendChild(this.baseRect(cx - width / 2, cy - height / 2, width, height, color, null));

    return group;
  }

}
