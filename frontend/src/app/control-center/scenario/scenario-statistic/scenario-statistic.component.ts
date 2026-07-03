import {Component, effect, inject, input, signal} from '@angular/core';
import {ScenarioService} from "../../../shared/scenario.service";
import {Scenario, ScenarioStatistic} from "../../../../shared/openapi-gen";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-scenario-statistic',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle
  ],
  templateUrl: './scenario-statistic.component.html',
  styleUrl: './scenario-statistic.component.css',
})
export class ScenarioStatisticComponent {

  private scenarioService = inject(ScenarioService);

  scenarioId = input.required<Number>();
  protected statistic = signal<ScenarioStatistic>({});
  protected scenario = signal<Scenario>({});

  constructor() {
    effect(() => {
      const scenarioId = this.scenarioId();
      this.scenarioService.loadScenarioStatistic(scenarioId).subscribe(data => {
        this.statistic.set(data);
      });
      this.scenarioService.loadScenario(scenarioId).subscribe(data => {
        this.scenario.set(data);
      });
    });
  }
}
