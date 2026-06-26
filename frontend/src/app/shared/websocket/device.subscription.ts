import {Injectable, Signal} from '@angular/core';
import {DeviceConnectionEvent, DeviceDataChangedEvent, DeviceInfoEvent} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";
import {toSignal} from "@angular/core/rxjs-interop";
import {map} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DeviceSubscription extends Subscription {

  readonly deviceConnection = this.createEventAccessor<DeviceConnectionEvent>('DeviceConnectionEvent');
  readonly isConnected: Signal<boolean> = toSignal(
    this.deviceConnection().pipe(
      map(event => event.connected ?? false)
    ),
    {initialValue: false}
  );

  readonly deviceInfo = this.createEventAccessor<DeviceInfoEvent>('DeviceInfoEvent');
  readonly deviceDataChanged = this.createEventAccessor<DeviceDataChangedEvent>('DeviceDataChangedEvent', true);

}
