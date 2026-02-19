import {Component, inject, OnInit, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {NgClass, NgForOf} from "@angular/common";
import {ScenarioService} from "../../../../shared/scenario.service";
import {ROUTERUNSTATE, RUNSTATE, Scenario} from "../../../../../shared/openapi-gen";
import {MatIcon} from "@angular/material/icon";
import {MatMiniFabButton} from "@angular/material/button";
import {DeviceService} from "../../../../shared/device.service";
import {ScenarioSubscription} from "../../../../shared/websocket/scenario.subscription";

export class RouteData {
  id: number;
  name: string;
  runState: ROUTERUNSTATE | null;

  constructor(id: number, name: string, runState: ROUTERUNSTATE | null) {
    this.id = id;
    this.name = name;
    this.runState = runState;
  }
}

export class ScenarioData {
  scenarioId: number;
  name: string;
  runState: RUNSTATE | null;
  scheduledExecutionTimeAsText: string;
  routes: Array<RouteData>;

  constructor(scenarioId: number, name: string, runState: RUNSTATE | null, scheduledExecutionTimeAsText: string,
              routes: Array<RouteData>) {
    this.scenarioId = scenarioId;
    this.name = name;
    this.runState = runState;
    this.scheduledExecutionTimeAsText = scheduledExecutionTimeAsText;
    this.routes = routes;
  }
}

@Component({
  selector: 'app-viewer-control-scenario',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    NgForOf,
    MatIcon,
    MatMiniFabButton,
    NgClass
  ],
  templateUrl: './scenario.component.html',
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent implements OnInit {
  private scenarioService = inject(ScenarioService);
  private scenarioSubscription = inject(ScenarioSubscription);
  protected scenarios = signal<ScenarioData[]>([]);

  protected isConnected = inject(DeviceService).isConnected;

  ngOnInit() {
    this.scenarioService.loadScenarios().subscribe(data => {
      this.mapData(data);

      this.scenarioSubscription.scenarioDataChanged().subscribe(() => {
        this.reloadScenarioData();
      });
      this.scenarioSubscription.routesChanged().subscribe(() => {
        this.reloadScenarioData();
      });

      this.scenarioSubscription.scenarioStateChanged().subscribe((event) => {
        let scenarioById = this.scenarioById(event.itemId!);
        scenarioById!.runState = event.state ?? null;
        scenarioById!.scheduledExecutionTimeAsText = event.nextScheduleTimeText ?? '-- : --';
      });
      this.scenarioSubscription.routeStateChanged().subscribe((event) => {
        if (event.state === ROUTERUNSTATE.Failed) {
          console.error('route error', event.state, event.message);
        }

        let routeSequences = this.scenarioById(event.scenarioId!)!.routes;
        let find = routeSequences!.find(r => r.id == event.routeSequenceId!);
        find!.runState = event.state ?? null;
      });
    })
  }

  private readonly EMPTY_TIME_TEXT = '--:--';

  private mapData(data: Scenario[]) {
    this.scenarios.set(data.map(scenario => {
      let routes = scenario.routeSequences?.map((routeSequence) => {
        return new RouteData(
          routeSequence.id!,
          routeSequence.route?.name!,
          routeSequence.route?.runState ?? null
        );
      });
      return new ScenarioData(
        scenario.id!,
        scenario.name!,
        scenario.runState ?? null,
        this.EMPTY_TIME_TEXT,
        routes!
      );
    }));
  }

  private scenarioById(scenarioId: number) {
    return this.scenarios().find(scenario => scenario.scenarioId === scenarioId);
  }

  private reloadScenarioData() {
    this.scenarioService.loadScenarios().subscribe(data => {
      this.mapData(data);
    });
  }

  protected scheduleScenario(scenarioId: number) {
    this.scenarioService.scheduleScenario(scenarioId);
  }

  protected startScenario(scenarioId: number) {
    this.scenarioService.startScenario(scenarioId);
  }

  protected stopScenario(scenarioId: number) {
    this.scenarioService.stopScenario(scenarioId);
  }

  protected scheduleAllScenarios() {
    this.scenarioService.scheduleAll();
  }

  protected stopAllScenarios() {
    this.scenarioService.stopAll();
  }

  protected readonly RUNSTATE = RUNSTATE;
}
