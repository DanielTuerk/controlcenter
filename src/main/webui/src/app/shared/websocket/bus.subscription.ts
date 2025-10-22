import {Injectable} from '@angular/core';
import {
  DeviceConnectionEvent,
  DeviceInfoEvent,
  FeedbackBlockEvent,
  PlayerEvent,
  RecordingEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class BusSubscription extends Subscription {

  readonly deviceConnection = this.createEventAccessor<DeviceConnectionEvent>('DeviceConnectionEvent');
  readonly deviceInfo = this.createEventAccessor<DeviceInfoEvent>('DeviceInfoEvent');

  readonly feedbackBlockEvent = this.createEventAccessor<FeedbackBlockEvent>('FeedbackBlockEvent');

  readonly player = this.createEventAccessor<PlayerEvent>('PlayerEvent');
  readonly recording = this.createEventAccessor<RecordingEvent>('RecordingEvent');

}
