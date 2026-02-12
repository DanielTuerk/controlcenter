import {Injectable} from '@angular/core';
import {
  SignalFunctionStateEvent,
  TrackBlockDataChangedEvent,
  TrackChangedEvent,
  TrackPartBlockEvent,
  TrackPartStateEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class TrackSubscription extends Subscription {

  readonly trackChanged = this.createEventAccessor<TrackChangedEvent>('TrackChangedEvent');
  readonly trackBlockDataChangedEvent = this.createEventAccessor<TrackBlockDataChangedEvent>('TrackBlockDataChangedEvent');

  readonly signalFunctionState = this.createEventAccessor<SignalFunctionStateEvent>('SignalFunctionStateEvent');
  readonly trackPartBlock = this.createEventAccessor<TrackPartBlockEvent>('TrackPartBlockEvent');
  readonly trackPartState = this.createEventAccessor<TrackPartStateEvent>('TrackPartStateEvent');

}
