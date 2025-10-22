import {Injectable} from '@angular/core';
import {
  RailVoltageEvent,
  SignalFunctionStateEvent,
  TrackPartBlockEvent,
  TrackPartStateEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class TrackSubscription extends Subscription {

  readonly railVoltage = this.createEventAccessor<RailVoltageEvent>('RailVoltageEvent');
  readonly signalFunctionState = this.createEventAccessor<SignalFunctionStateEvent>('SignalFunctionStateEvent');
  readonly trackPartBlock = this.createEventAccessor<TrackPartBlockEvent>('TrackPartBlockEvent');
  readonly trackPartState = this.createEventAccessor<TrackPartStateEvent>('TrackPartStateEvent');

}
