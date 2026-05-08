import {Component, inject, input, OnInit, signal} from '@angular/core';
import {TrainService} from "../../../shared/train.service";
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {Scenario, Train} from "../../../../shared/openapi-gen";
import {SnackBar} from "../../common/snack-bar.component";
import {ScenarioService} from "../../../shared/scenario.service";

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
    MatButton
  ],
  templateUrl: './scenario-edit.component.html',
  styleUrl: './scenario-edit.component.css'
})
export class ScenarioEditComponent implements OnInit {
  scenarioId = input.required<Number>();
  private scenarioService = inject(ScenarioService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  scenario = signal<Scenario>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    cron: '',
  });

  ngOnInit() {

    this.scenarioService.loadScenario(this.scenarioId()).subscribe(data => {
      this.setScenario(data);
    });
  }

  onSubmit() {

    let scenarioToUpdate = this.scenario();
    scenarioToUpdate.name = this.form.controls.name.getRawValue()!
    scenarioToUpdate.cron = this.form.controls.cron.getRawValue()!

    let observable;
    let operation;
    if (scenarioToUpdate.id === undefined) {
      observable = this.scenarioService.createScenario(scenarioToUpdate);
      operation='created'
    } else {
      observable = this.scenarioService.saveScenario(scenarioToUpdate);
      operation='updated'
    }
    observable.subscribe(data => {
      this.snackBar.showSuccess(`scenario "${scenarioToUpdate.name}" ${operation} successfully.`);
      this.router.navigate(['/cc/scenario', {}]);
    })
  }

  private setScenario(scenario: Scenario) {
    this.scenario.set(scenario);
    this.form.controls.name.setValue(scenario.name!)
    this.form.controls.cron.setValue(scenario.cron!)
  }
}
