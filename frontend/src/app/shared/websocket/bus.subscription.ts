import {Injectable, Signal} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';
import {
  BusDataEvent,
  PlayerEvent,
  RailVoltageEvent,
  RecordingEvent,
  SYSTEMFORMAT,
  SystemFormatEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class BusSubscription extends Subscription {

  private readonly _railvoltage = this.createEventAccessor<RailVoltageEvent>('RailVoltageEvent');
  readonly railVoltage: Signal<boolean> = toSignal(
    this._railvoltage().pipe(
      map(event => event.state ?? false)
    ),
    {initialValue: false}
  );

  private readonly _systemFormat = this.createEventAccessor<SystemFormatEvent>('SystemFormatEvent');
  readonly systemFormat: Signal<SYSTEMFORMAT> = toSignal(
    this._systemFormat().pipe(
      map(event => event.systemFormat ? event.systemFormat : SYSTEMFORMAT.Unknown)
    ),
    {initialValue: SYSTEMFORMAT.Unknown}
  );

  private readonly _player = this.createEventAccessor<PlayerEvent>('PlayerEvent');
  readonly playerState = toSignal(this._player());

  private readonly _recording = this.createEventAccessor<RecordingEvent>('RecordingEvent');
  readonly recordingState = toSignal(this._recording());

  readonly busDataEvent = this.createEventAccessor<BusDataEvent>('BusDataEvent', true);

}
