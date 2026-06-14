import {Injectable} from '@angular/core';
import {
  BusDataEvent,
  PlayerEvent,
  RailVoltageEvent,
  RecordingEvent,
  SystemFormatEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class BusSubscription extends Subscription {

  readonly railvoltage = this.createEventAccessor<RailVoltageEvent>('RailVoltageEvent');
  readonly systemFormat = this.createEventAccessor<SystemFormatEvent>('SystemFormatEvent');
  readonly player = this.createEventAccessor<PlayerEvent>('PlayerEvent');
  readonly recording = this.createEventAccessor<RecordingEvent>('RecordingEvent');
  readonly busDataEvent = this.createEventAccessor<BusDataEvent>('BusDataEvent', true);

}
