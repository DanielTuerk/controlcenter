import {ChangeDetectionStrategy, Component, effect, inject, input, signal} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {DRIVINGDIRECTION1, Route, RouteSequence, Scenario, Train} from "../../../../shared/openapi-gen";
import {SnackBar} from "../../common/snack-bar.component";
import {ScenarioService} from "../../../shared/scenario.service";
import {TrainService} from "../../../shared/train.service";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";

import {MatList, MatListItem} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {RouteService} from "../../../shared/route.service";

@Component({
  selector: 'app-scenario-edit',
  imports: [
    RouterLink,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    ReactiveFormsModule,
    MatInput,
    MatFormField,
    FormsModule,
    MatCardFooter,
    MatButton,
    MatOption,
    MatSelect,
    MatList,
    MatListItem,
    MatIcon,
    MatMiniFabButton,
    MatLabel
],
  templateUrl: './scenario-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './scenario-edit.component.css'
})
export class ScenarioEditComponent {
  scenarioId = input.required<Number>();
  private scenarioService = inject(ScenarioService);
  private trainService = inject(TrainService);
  private routeService = inject(RouteService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  scenario = signal<Scenario>({});
  trains = signal<Train[]>([]);
  routes = signal<Route[]>([]);

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    cron: '',
    train: new FormControl<Train | null>(null),
    drivingDirection: new FormControl<DRIVINGDIRECTION1 | null>(null),
    drivingLevel: 0,
    routeSequences: new FormControl<RouteSequence[]>([])
  });

  constructor() {
    effect(() => {
      const scenarioId = this.scenarioId();
      if (scenarioId && scenarioId.toString() != "create") {
        this.scenarioService.loadScenario(scenarioId).subscribe(data => {
          this.setScenario(data);
        });
      }
    });
    this.loadRoutes();
    this.loadTrains();
  }

  private loadTrains() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data);
      this.form.controls.train.setValue(this.trains().find(b => b.id === this.scenario().train?.id) ?? null)
    });
  }

  private loadRoutes() {
    this.routeService.loadRoutes().subscribe(data => {
      this.routes.set(data);
    });
  }

  private setScenario(scenario: Scenario) {
    this.scenario.set(scenario);
    this.form.controls.name.setValue(scenario.name!);
    this.form.controls.cron.setValue(scenario.cron!);
    this.form.controls.train.setValue(this.trains().find(b => b.id === scenario.train?.id) ?? null);
    this.form.controls.drivingDirection.setValue(scenario.trainDrivingDirection!);
    this.form.controls.drivingLevel.setValue(scenario.startDrivingLevel!);
  }

  protected readonly DRIVINGDIRECTION1 = DRIVINGDIRECTION1;

  protected addRoute(selectedRoute: Route | undefined) {
    if (selectedRoute) {
      this.scenario.update(scenario => {
        const routeSequences = scenario.routeSequences ?? [];
        return {
          ...scenario,
          routeSequences: [...routeSequences, {
            id: undefined,
            route: selectedRoute,
            position: routeSequences.length,
            endDelayInSeconds: 0
          }]
        };
      });
    }
  }

  onSubmit() {
    let scenarioToUpdate = this.scenario();
    scenarioToUpdate.name = this.form.controls.name.getRawValue()!
    scenarioToUpdate.cron = this.form.controls.cron.getRawValue()!
    scenarioToUpdate.train = this.form.controls.train.getRawValue()!
    scenarioToUpdate.trainDrivingDirection = this.form.controls.drivingDirection.getRawValue()!
    scenarioToUpdate.startDrivingLevel = this.form.controls.drivingLevel.getRawValue()!

    let existingRouteSequences = this.scenario().routeSequences;
    if (existingRouteSequences) {
      let routeSequences = existingRouteSequences!;
    scenarioToUpdate.routeSequences = routeSequences.map(rs => {
      rs.position = routeSequences.indexOf(rs);
      return rs;
    });
    }

    let observable;
    let operation;
    if (scenarioToUpdate.id === undefined) {
      observable = this.scenarioService.createScenario(scenarioToUpdate);
      operation='created'
    } else {
      observable = this.scenarioService.saveScenario(scenarioToUpdate);
      operation='updated'
    }
    observable.subscribe(() => {
      this.snackBar.showSuccess(`scenario "${scenarioToUpdate.name}" ${operation} successfully.`);
      this.router.navigate(['/cc/scenario', {}]);
    })
  }

  protected removeRouteSequence(opt: RouteSequence) {
    this.scenario.update(scenario => {
      const routeSequences = scenario.routeSequences;
      if (!routeSequences) {
        return scenario;
      }
      const index = routeSequences.indexOf(opt);
      if (index < 0) {
        return scenario;
      }
      const updated = [...routeSequences];
      updated.splice(index, 1);
      return {...scenario, routeSequences: updated};
    });
  }

  protected moveUpRouteSequence(opt: RouteSequence): void {
    this.moveRouteSequence(opt, -1);
  }

  protected moveDownRouteSequence(opt: RouteSequence): void {
    this.moveRouteSequence(opt, 1);
  }

  protected moveRouteSequence(opt: RouteSequence, direction: -1 | 1): void {
    this.scenario.update(scenario => {
      const routeSequences = scenario.routeSequences;
      if (!routeSequences?.length) {
        return scenario;
      }

      const index = routeSequences.indexOf(opt);
      if (index < 0) {
        return scenario;
      }

      const targetIndex = index + direction;
      if (targetIndex < 0 || targetIndex >= routeSequences.length) {
        return scenario;
      }

      const updated = [...routeSequences];
      [updated[index], updated[targetIndex]] = [updated[targetIndex], updated[index]];
      return {...scenario, routeSequences: updated};
    });
  }
}
