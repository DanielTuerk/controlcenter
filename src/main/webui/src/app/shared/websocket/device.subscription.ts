import {Injectable} from '@angular/core';
import {DeviceConnectionEvent, DeviceDataChangedEvent, DeviceInfoEvent} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class DeviceSubscription extends Subscription {

  readonly deviceConnection = this.createEventAccessor<DeviceConnectionEvent>('DeviceConnectionEvent');
  readonly deviceInfo = this.createEventAccessor<DeviceInfoEvent>('DeviceInfoEvent');
  readonly deviceDataChanged = this.createEventAccessor<DeviceDataChangedEvent>('DeviceDataChangedEvent');

}
