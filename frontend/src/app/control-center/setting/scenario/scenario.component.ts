import {Component} from '@angular/core';
import {MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {ReactiveFormsModule} from "@angular/forms";
import {ConfigInputComponent} from "../config-input/config-input.component";
import {MatList, MatListItem} from "@angular/material/list";

@Component({
  selector: 'app-scenario',
  imports: [
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    ReactiveFormsModule,
    ConfigInputComponent,
    MatListItem,
    MatList
  ],
  templateUrl: './scenario.component.html',
  styleUrl: './scenario.component.css'
})
export class ScenarioComponent {

  protected readonly HP_0_AFTER_TRAIN_PASS_DELAY_IN_SECONDS = "HP0_AFTER_TRAIN_PASS_DELAY_IN_SECONDS";
  protected readonly START_TRAIN_DELAY_SECONDS = "START_TRAIN_DELAY_SECONDS";
  protected readonly FINISH_ROUTE_DELAY_SECONDS = "FINISH_ROUTE_DELAY_SECONDS";
  protected readonly DEFAULT_START_DRIVING_LEVEL = "DEFAULT_START_DRIVING_LEVEL";
  protected readonly WAIT_FOR_FREE_TACK_TIMEOUT_IN_MINUTES = "WAIT_FOR_FREE_TACK_TIMEOUT_IN_MINUTES";
}
