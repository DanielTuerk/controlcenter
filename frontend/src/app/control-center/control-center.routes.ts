import {Routes} from "@angular/router";
import {TrainComponent} from "./train/train.component";
import {TrainEditComponent} from "./train/train-edit/train-edit.component";
import {ScenarioComponent} from "./scenario/scenario.component";

export const routes: Routes = [
  {
    path: 'train',
    component: TrainComponent
  },
  {
    path: 'train/:trainId',
    component: TrainEditComponent
  },
  {
    path: 'scenario',
    component: ScenarioComponent
  }
];
