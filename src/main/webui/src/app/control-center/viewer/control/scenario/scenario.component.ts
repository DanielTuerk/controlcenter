import {Component, inject, OnInit, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {NgForOf} from "@angular/common";
import {ScenarioService} from "../../../../shared/scenario.service";
import {Scenario} from "../../../../../shared/openapi-gen";
import {CronExpressionParser} from "cron-parser";

@Component({
  selector: 'app-viewer-control-scenario',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    NgForOf
  ],
  templateUrl: './scenario.component.html',
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent implements OnInit {
  private scenarioService = inject(ScenarioService);
  scenarios = signal<Scenario[]>([]);

  ngOnInit() {
    this.scenarioService.loadScenarios().subscribe(data => {
      this.scenarios.set(data);
    })
  }

  nextExecutionTime(scenario: Scenario) {
    if (scenario.cron
      && scenario.runState === 'IDLE'
      && scenario.mode === 'AUTOMATIC') {
      return CronExpressionParser.parse(scenario.cron).next().toDate().toLocaleTimeString('de-DE', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      });
    }
    return '--:--';
  }
}
