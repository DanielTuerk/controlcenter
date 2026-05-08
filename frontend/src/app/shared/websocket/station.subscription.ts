import {Injectable} from '@angular/core';
import {StationBoardChangedEvent, StationDataChangedEvent} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class StationSubscription extends Subscription {

  readonly stationBoardChanged = this.createEventAccessor<StationBoardChangedEvent>('StationBoardChangedEvent');
  readonly stationDataChanged = this.createEventAccessor<StationDataChangedEvent>('StationDataChangedEvent');

}
