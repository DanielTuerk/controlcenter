import {Injectable} from '@angular/core';
import {
  RoutesChangedEvent,
  RouteStateEvent,
  ScenarioDataChangedEvent,
  ScenarioStateEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class ScenarioSubscription extends Subscription {

  readonly routesChanged = this.createEventAccessor<RoutesChangedEvent>('RoutesChangedEvent', false);
  readonly scenarioDataChanged = this.createEventAccessor<ScenarioDataChangedEvent>('ScenarioDataChangedEvent', false);

  readonly routeStateChanged = this.createEventAccessor<RouteStateEvent>('RouteStateEvent', true);
  readonly scenarioStateChanged = this.createEventAccessor<ScenarioStateEvent>('ScenarioStateEvent', true);

}
