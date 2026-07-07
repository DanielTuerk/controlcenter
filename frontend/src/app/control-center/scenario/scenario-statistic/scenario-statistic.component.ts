import {Component, computed, effect, inject, input, signal} from '@angular/core';
import {DatePipe, formatDate} from '@angular/common';
import {ScenarioService} from "../../../shared/scenario.service";
import {Scenario, ScenarioStatistic} from "../../../../shared/openapi-gen";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {SuccessIconComponent} from "../../../shared/component/SuccessIconComponent";
import {ErrorIconComponent} from "../../../shared/component/ErrorIconComponent";

interface RunBar {
  widthPercent: number;
  valueDate: Date;
  tooltip: string;
}

interface ChartTick {
  position: number;
  value: Date;
}

/** Rounds up to a "clean" 1/2/5/10 step so chart gridlines land on readable values. */
function niceCeilMillis(millis: number): number {
  if (millis <= 0) {
    return 1000;
  }
  const seconds = millis / 1000;
  const exponent = Math.floor(Math.log10(seconds));
  const base = Math.pow(10, exponent);
  for (const step of [1, 2, 5, 10]) {
    const candidate = step * base;
    if (candidate >= seconds) {
      return candidate * 1000;
    }
  }
  return 10 * base * 1000;
}

@Component({
  selector: 'app-scenario-statistic',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    SuccessIconComponent,
    ErrorIconComponent,
    DatePipe
  ],
  templateUrl: './scenario-statistic.component.html',
  styleUrl: './scenario-statistic.component.css',
})
export class ScenarioStatisticComponent {

  private scenarioService = inject(ScenarioService);

  scenarioId = input.required<Number>();
  protected statistic = signal<ScenarioStatistic>({});
  protected scenario = signal<Scenario>({});
  protected averageRunTime = computed(() => new Date(this.statistic().averageRunTimeInMillis ?? 0));

  private successfulRuns = computed(() =>
    (this.statistic().runs ?? []).filter(run => run.state === 'SUCCESS'));

  protected chartMax = computed(() =>
    niceCeilMillis(Math.max(0, ...this.successfulRuns().map(run => run.averageRunTimeInMillis ?? 0))));

  protected chartTicks = computed<ChartTick[]>(() => {
    const max = this.chartMax();
    return [0, 0.25, 0.5, 0.75, 1].map(fraction => ({
      position: fraction * 100,
      value: new Date(max * fraction),
    }));
  });

  protected runBars = computed<RunBar[]>(() => {
    const max = this.chartMax();
    return this.successfulRuns().map(run => {
      const millis = run.averageRunTimeInMillis ?? 0;
      const start = run.start ? formatDate(run.start, 'dd.MM.yyyy HH:mm:ss', 'en-US') : 'unknown start';
      const duration = formatDate(new Date(millis), 'HH:mm:ss', 'en-US', 'UTC');
      return {
        widthPercent: max > 0 ? (millis / max) * 100 : 0,
        valueDate: new Date(millis),
        tooltip: `${start} · ${duration}`,
      };
    });
  });

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
