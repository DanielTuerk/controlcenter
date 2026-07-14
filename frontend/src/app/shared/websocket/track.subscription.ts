import {Injectable} from '@angular/core';
import {
  SignalFunctionStateEvent,
  TrackBlockDataChangedEvent,
  TrackChangedEvent,
  TrackPartBlockEvent,
  TrackPartDataChangedEvent,
  TrackPartStateEvent,
  TrainInBlockEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class TrackSubscription extends Subscription {

  readonly trackChanged = this.createEventAccessor<TrackChangedEvent>('TrackChangedEvent');
  readonly trackBlockDataChangedEvent = this.createEventAccessor<TrackBlockDataChangedEvent>('TrackBlockDataChangedEvent');
  readonly trackPartDataChangedEvent = this.createEventAccessor<TrackPartDataChangedEvent>('TrackPartDataChangedEvent');

  readonly signalFunctionState = this.createEventAccessor<SignalFunctionStateEvent>('SignalFunctionStateEvent', true);
  readonly trackPartState = this.createEventAccessor<TrackPartStateEvent>('TrackPartStateEvent');

  readonly trackPartBlock = this.createEventAccessor<TrackPartBlockEvent>('TrackPartBlockEvent');
  readonly trainInBlockEvent = this.createEventAccessor<TrainInBlockEvent>('TrainInBlockEvent');

}
