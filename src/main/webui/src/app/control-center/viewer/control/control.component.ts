import {Component} from '@angular/core';
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {TrainComponent} from "./train/train.component";
import {ScenarioComponent} from "./scenario/scenario.component";

@Component({
  selector: 'app-viewer-control',
  imports: [
    MatTabGroup,
    MatTab,
    TrainComponent,
    ScenarioComponent,
  ],
  templateUrl: './control.component.html',
  styleUrl: './control.component.css'
})
export class ControlComponent {

}
