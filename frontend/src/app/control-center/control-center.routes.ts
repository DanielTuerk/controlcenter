import {Routes} from "@angular/router";
import {TrainComponent} from "./train/train.component";
import {TrainEditComponent} from "./train/train-edit/train-edit.component";
import {ScenarioComponent} from "./scenario/scenario.component";
import {ViewerComponent} from "./viewer/viewer.component";
import {EditorComponent} from "./editor/editor.component";
import {SettingComponent} from "./setting/setting.component";

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
  },
  {
    path: 'viewer',
    component: ViewerComponent
  },
  {
    path: 'editor',
    component: EditorComponent
  },
  {
    path: 'settings',
    component: SettingComponent
  }
];
