import {AbstractTrackPart} from "../../../../shared/openapi-gen";

export interface TrackElement<E> {
  trackPart: AbstractTrackPart;
  svgElement: Element;
  lastEvent: E | null;
}
