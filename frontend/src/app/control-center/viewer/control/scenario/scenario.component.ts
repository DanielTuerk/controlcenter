import {ChangeDetectionStrategy, Component, inject, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {NgClass} from "@angular/common";
import {ScenarioService} from "../../../../shared/scenario.service";
import {ROUTERUNSTATE, RUNSTATE, Scenario} from "../../../../../shared/openapi-gen";
import {MatIcon} from "@angular/material/icon";
import {MatMiniFabButton} from "@angular/material/button";
import {DeviceService} from "../../../../shared/device.service";
import {ScenarioSubscription} from "../../../../shared/websocket/scenario.subscription";
import {CronExpressionParser} from "cron-parser";
import {SnackBar} from "../../../common/snack-bar.component";
import {RouteRunStateIconComponent} from "../../../common/route-run-state-icon";
import {ScenarioRunStateIconComponent} from "../../../common/scenario-run-state-icon";

export class RouteData {
  id: number;
  name: string;
  runState: ROUTERUNSTATE | null;
  message: string | null;

  constructor(id: number, name: string, runState: ROUTERUNSTATE | null, message: string | null) {
    this.id = id;
    this.name = name;
    this.runState = runState;
    this.message = message;
  }
}

export class ScenarioData {
  scenarioId: number;
  name: string;
  runState: RUNSTATE | null;
  cron: string | null;
  scheduledExecutionTimeAsText: string;
  routes: Array<RouteData>;

  constructor(scenarioId: number, name: string, runState: RUNSTATE | null, routes: Array<RouteData>) {
    this.scenarioId = scenarioId;
    this.name = name;
    this.runState = runState;
    this.routes = routes;
    this.scheduledExecutionTimeAsText = '-- : --';
    this.cron = null
  }
}

@Component({
  selector: 'app-viewer-control-scenario',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon,
    MatMiniFabButton,
    NgClass,
    RouteRunStateIconComponent,
    ScenarioRunStateIconComponent
  ],
  templateUrl: './scenario.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent {
  private static readonly EMPTY_TIME_TEXT = '--:--';
  private scenarioService = inject(ScenarioService);
  private scenarioSubscription = inject(ScenarioSubscription);
  private snackBar = inject(SnackBar);

  protected scenarios = signal<ScenarioData[]>([]);

  protected isConnected = inject(DeviceService).isConnected;

  constructor() {
    this.scenarioService.loadScenarios().subscribe(data => {
      this.mapData(data);

      this.scenarioSubscription.scenarioDataChanged().subscribe(() => {
        this.reloadScenarioData();
      });
      this.scenarioSubscription.scenariosChanged().subscribe(() => {
        this.reloadScenarioData();
      });

      this.scenarioSubscription.scenarioSchedule().subscribe((event) => {
        let scenarioById = this.scenarioById(event.scenarioId!);

        let scheduled = event.on ?? false;
        if (scheduled) {
          scenarioById!.cron = event.cron!;
        } else {
          scenarioById!.cron = null;
        }
        scenarioById!.scheduledExecutionTimeAsText = this.timeStringFromCron(scenarioById!.cron);
      });

      this.scenarioSubscription.scenarioStateChanged().subscribe((event) => {
        let scenarioById = this.scenarioById(event.itemId!);
        scenarioById!.runState = event.state ?? null;
        scenarioById!.scheduledExecutionTimeAsText = this.timeStringFromCron(scenarioById!.cron);

        let msg = `scenario ${scenarioById!.name} (${scenarioById!.scenarioId})`;
        switch (event.state) {
          case RUNSTATE.Failed:
            this.snackBar.showError(msg + ' failed');
            break;
          case RUNSTATE.Stopped:
            this.snackBar.showMessage(msg + ' stopped');
            break;
        }

      });
      this.scenarioSubscription.routeStateChanged().subscribe((event) => {
        if (event.state === ROUTERUNSTATE.Failed) {
          console.error('route error', event.state, event.message);
        }

        let routeSequences = this.scenarioById(event.scenarioId!)!.routes;
        let find = routeSequences!.find(r => r.id == event.routeSequenceId!);
        find!.runState = event.state ?? null;
        find!.message = event.message ?? null;
      });
    })
  }

  private timeStringFromCron(cron: string | null): string {
    return cron === null ? ScenarioComponent.EMPTY_TIME_TEXT : this.cronToFixedTime(cron!);
  }

  private mapData(data: Scenario[]) {
    this.scenarios.set(data.map(scenario => {
      let routes = scenario.routeSequences?.map((routeSequence) => {
        return new RouteData(
          routeSequence.id!,
          routeSequence.route?.name!,
          null,
          null
        );
      });
      return new ScenarioData(
        scenario.id!,
        scenario.name!,
        null,
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

  protected scheduleScenario(scenario: ScenarioData) {
    this.scenarioService.scheduleScenario(scenario);
  }

  protected startScenario(scenario: ScenarioData) {
    this.scenarioService.startScenario(scenario);
  }

  protected stopScenario(scenario: ScenarioData) {
    this.scenarioService.stopScenario(scenario);
  }

  protected scheduleAllScenarios() {
    this.scenarioService.scheduleAll();
  }

  protected stopAllScenarios() {
    this.scenarioService.stopAll();
  }

  protected canBeStarted(scenarioId: number) {
    return this.isConnected() &&
      (this.scenarioById(scenarioId)?.runState !== RUNSTATE.Running
        && this.scenarioById(scenarioId)?.runState !== RUNSTATE.Scheduled);
  }

  protected canBeScheduled(scenarioId: number) {
    return this.canBeStarted(scenarioId);
  }

  protected canBeStopped(scenarioId: number) {
    return this.isConnected() &&
      (this.scenarioById(scenarioId)?.runState === RUNSTATE.Running
        || this.scenarioById(scenarioId)?.runState === RUNSTATE.Scheduled);
  }

  private cronToFixedTime(cron: string): string {
    const interval = CronExpressionParser.parse(cron);
    const nextDate = interval.next().toDate();

    const hours = String(nextDate.getHours()).padStart(2, '0');
    const minutes = String(nextDate.getMinutes()).padStart(2, '0');

    return `${hours}:${minutes}`;
  }

  protected getBgColor(scenario:ScenarioData) {
    if(!scenario.runState) return '';
    switch (scenario.runState) {
      case RUNSTATE.Scheduled: return 'background-color: #1e3a53';
      case RUNSTATE.Running: return 'background-color: blue';
      case RUNSTATE.Success: return 'background-color: #1e7724';
      case RUNSTATE.Paused: return 'background-color: yellow';
      case RUNSTATE.Failed: return 'background-color: #6e3333';
      default: return '';
    }
  }
}
