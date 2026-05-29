import {Component, inject, input, OnInit, signal} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {DRIVINGDIRECTION, DRIVINGDIRECTION1, Scenario, SIGNALTYPE, Train} from "../../../../shared/openapi-gen";
import {SnackBar} from "../../common/snack-bar.component";
import {ScenarioService} from "../../../shared/scenario.service";
import {TrainService} from "../../../shared/train.service";
import {TrainSubscription} from "../../../shared/websocket/train.subscription";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {NgForOf} from "@angular/common";

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
    NgForOf
  ],
  templateUrl: './scenario-edit.component.html',
  styleUrl: './scenario-edit.component.css'
})
export class ScenarioEditComponent implements OnInit {
  scenarioId = input.required<Number>();
  private scenarioService = inject(ScenarioService);
  private trainService = inject(TrainService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);
  private trainSubscription = inject(TrainSubscription);

  scenario = signal<Scenario>({});
  trains = signal<Train[]>([]);

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
  });

  ngOnInit() {
    if (this.scenarioId() && this.scenarioId().toString() != "create") {
      this.scenarioService.loadScenario(this.scenarioId()).subscribe(data => {
        this.setScenario(data);
      });
    }
    this.trainSubscription.trainDataChanged().subscribe(() => {
      this.loadTrains();
    });
    this.loadTrains();
  }

  private loadTrains() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data);
      this.form.controls.train.setValue(this.trains().find(b => b.id === this.scenario().train?.id) ?? null)
    });
  }

  onSubmit() {
    let scenarioToUpdate = this.scenario();
    scenarioToUpdate.name = this.form.controls.name.getRawValue()!
    scenarioToUpdate.cron = this.form.controls.cron.getRawValue()!
    scenarioToUpdate.train = this.form.controls.train.getRawValue()!
    scenarioToUpdate.trainDrivingDirection = this.form.controls.drivingDirection.getRawValue()!
    scenarioToUpdate.startDrivingLevel = this.form.controls.drivingLevel.getRawValue()!

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

  private setScenario(scenario: Scenario) {
    this.scenario.set(scenario);
    this.form.controls.name.setValue(scenario.name!)
    this.form.controls.cron.setValue(scenario.cron!)
    this.form.controls.train.setValue(this.trains().find(b => b.id === scenario.train?.id) ?? null)
    this.form.controls.drivingDirection.setValue(scenario.trainDrivingDirection!)
    this.form.controls.drivingLevel.setValue(scenario.startDrivingLevel!)
  }

  protected readonly SIGNALTYPE = SIGNALTYPE;
  protected readonly DRIVINGDIRECTION = DRIVINGDIRECTION;
  protected readonly DRIVINGDIRECTION1 = DRIVINGDIRECTION1;
}
