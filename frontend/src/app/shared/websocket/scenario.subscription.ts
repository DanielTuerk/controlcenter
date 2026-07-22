import {Injectable} from '@angular/core';
import {
  RouteDataChangedEvent,
  RouteStateEvent,
  ScenarioDataChangedEvent,
  ScenarioScheduleEvent,
  ScenarioStateEvent
} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class ScenarioSubscription extends Subscription {

  readonly scenarioDataChanged = this.createEventAccessor<ScenarioDataChangedEvent>('ScenarioDataChangedEvent', false);
  readonly scenariosChanged = this.createEventAccessor<ScenarioDataChangedEvent>('ScenariosChangedEvent', false);
  readonly scenarioStateChanged = this.createEventAccessor<ScenarioStateEvent>('ScenarioStateEvent', true);
  readonly scenarioSchedule = this.createEventAccessor<ScenarioScheduleEvent>('ScenarioScheduleEvent', true);

  readonly routesChanged = this.createEventAccessor<RouteDataChangedEvent>('RoutesChangedEvent', false);
  readonly routeDataChanged = this.createEventAccessor<RouteDataChangedEvent>('RouteDataChangedEvent', false);
  readonly routeStateChanged = this.createEventAccessor<RouteStateEvent>('RouteStateEvent', true);

}
