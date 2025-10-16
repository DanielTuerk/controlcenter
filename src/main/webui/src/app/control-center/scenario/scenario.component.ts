import {Component, inject, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {Scenario} from "../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ScenarioService} from "../../shared/scenario.service";
import {ConfirmDialogComponent} from "../common/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-scenario',
  imports: [
    RouterLink,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatTableModule,
    MatIcon,
    MatButton
  ],
  templateUrl: './scenario.component.html',
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent implements OnInit {
  private scenarioService = inject(ScenarioService);
  scenarios = signal<Scenario[]>([]);
  displayedColumns: string[] = ['id', 'name', 'cron', 'action'];
  readonly dialog = inject(MatDialog);

  ngOnInit() {
    this.scenarioService.loadScenarios().subscribe(data => {
      this.scenarios.set(data);
    })
  }

  deleteScenario(scenario: Scenario) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: scenario.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.scenarioService.deleteScenario(scenario.id!);
      }
    });
  }
}
