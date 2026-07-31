import {Injectable} from '@angular/core';
import {
  StationBoardArrivalEvent,
  StationBoardDepartureEvent,
  StationDataChangedEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class StationSubscription extends Subscription {

  readonly stationBoardArrival = this.createEventAccessor<StationBoardArrivalEvent>('StationBoardArrivalEvent', true);
  readonly stationBoardDeparture = this.createEventAccessor<StationBoardDepartureEvent>('StationBoardDepartureEvent', true);
  readonly stationDataChanged = this.createEventAccessor<StationDataChangedEvent>('StationDataChangedEvent');

}
