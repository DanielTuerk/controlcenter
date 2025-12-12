import {Routes} from "@angular/router";
import {TrainComponent} from "./train/train.component";
import {TrainEditComponent} from "./train/train-edit/train-edit.component";
import {ScenarioComponent} from "./scenario/scenario.component";
import {ViewerComponent} from "./viewer/viewer.component";
import {EditorComponent} from "./editor/editor.component";
import {SettingComponent} from "./setting/setting.component";
import {ScenarioEditComponent} from "./scenario/scenario-edit/scenario-edit.component";
import {routes as settingRoutes} from "./setting/setting.routes";
import {DeviceComponent} from "./setting/device/device.component";
import {DeviceEditComponent} from "./setting/device-edit/device-edit.component";

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
    path: 'scenario/:scenarioId',
    component: ScenarioEditComponent
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
    component: SettingComponent,
    children: settingRoutes
  }
];
