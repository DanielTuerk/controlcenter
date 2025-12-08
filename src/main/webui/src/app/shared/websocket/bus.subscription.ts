import {Injectable} from '@angular/core';
import {
  DeviceConnectionEvent,
  DeviceInfoEvent,
  FeedbackBlockEvent,
  PlayerEvent, RailVoltageEvent,
  RecordingEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class BusSubscription extends Subscription {

  readonly feedbackBlockEvent = this.createEventAccessor<FeedbackBlockEvent>('FeedbackBlockEvent');

  readonly railvoltage = this.createEventAccessor<RailVoltageEvent>('RailVoltageEvent');
  readonly player = this.createEventAccessor<PlayerEvent>('PlayerEvent');
  readonly recording = this.createEventAccessor<RecordingEvent>('RecordingEvent');

}
