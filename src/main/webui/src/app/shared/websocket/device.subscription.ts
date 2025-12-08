import {Injectable} from '@angular/core';
import {
  DeviceConnectionEvent,
  DeviceInfoEvent,
  FeedbackBlockEvent,
  PlayerEvent,
  RecordingEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DeviceSubscription extends Subscription {

  readonly deviceConnection = this.createEventAccessor<DeviceConnectionEvent>('DeviceConnectionEvent');
  readonly deviceInfo = this.createEventAccessor<DeviceInfoEvent>('DeviceInfoEvent');

}
