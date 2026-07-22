import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {merge, of, switchMap} from 'rxjs';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {DRIVINGDIRECTION1, Scenario} from "../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ScenarioService} from "../../shared/scenario.service";
import {ConfirmDialogComponent} from "../common/confirm-dialog/confirm-dialog.component";
import {RouteComponent} from "./route/route.component";
import {TrainDirectionIcon} from "../common/trainDirectionIcon";
import {ScenarioSubscription} from "../../shared/websocket/scenario.subscription";

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
    MatButton,
    MatIconButton,
    RouteComponent,
    TrainDirectionIcon
  ],
  templateUrl: './scenario.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent {
  private scenarioService = inject(ScenarioService);
  private scenarioSubscription = inject(ScenarioSubscription);
  private dialog = inject(MatDialog);

  scenarios = toSignal(
    merge(of(null),
      this.scenarioSubscription.scenarioDataChanged(),
      this.scenarioSubscription.scenariosChanged())
      .pipe(
        switchMap(() => this.scenarioService.loadScenarios())
      ),
    {initialValue: []}
  );

  displayedColumns: string[] = ['id', 'name', 'train', 'cron', 'action'];

  deleteScenario(scenario: Scenario) {
    this.dialog.open(ConfirmDialogComponent, {data: scenario.name})
      .afterClosed().subscribe(result => {
      if (result === true) this.scenarioService.deleteScenario(scenario.id!);
    });
  }

  protected readonly DRIVINGDIRECTION1 = DRIVINGDIRECTION1;
}
