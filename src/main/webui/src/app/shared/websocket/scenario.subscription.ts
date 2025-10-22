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

  readonly routesChanged = this.createEventAccessor<RoutesChangedEvent>('RoutesChangedEvent');
  readonly scenarioDataChanged = this.createEventAccessor<ScenarioDataChangedEvent>('ScenarioDataChangedEvent');

  readonly routeState = this.createEventAccessor<RouteStateEvent>('RouteStateEvent');
  readonly scenarioState = this.createEventAccessor<ScenarioStateEvent>('ScenarioStateEvent');

}
