import {Injectable} from '@angular/core';
import {
  TrainDataChangedEvent,
  TrainDrivingDirectionEvent,
  TrainDrivingLevelEvent,
  TrainFunctionStateEvent,
  TrainHornStateEvent,
  TrainLightStateEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class TrainSubscription extends Subscription {

  readonly trainDataChanged = this.createEventAccessor<TrainDataChangedEvent>('TrainDataChangedEvent');

  readonly trainDrivingLevel = this.createEventAccessor<TrainDrivingLevelEvent>('TrainDrivingLevelEvent');
  readonly trainDrivingDirection = this.createEventAccessor<TrainDrivingDirectionEvent>('TrainDrivingDirectionEvent');
  readonly trainHornState = this.createEventAccessor<TrainHornStateEvent>('TrainHornStateEvent');
  readonly trainLightState = this.createEventAccessor<TrainLightStateEvent>('TrainLightStateEvent');
  readonly trainFunctionState = this.createEventAccessor<TrainFunctionStateEvent>('TrainFunctionStateEvent');

}
