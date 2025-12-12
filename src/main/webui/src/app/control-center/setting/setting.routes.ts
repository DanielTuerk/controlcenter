import {Routes} from "@angular/router";
import {DeviceComponent} from "./device/device.component";
import {DeviceEditComponent} from "./device-edit/device-edit.component";
import {CommonComponent} from "./common/common.component";
import {ConstructionComponent} from "./construction/construction.component";

export const routes: Routes = [
  {
    path: 'device',
    component: DeviceComponent
  },
  {
    path: 'device/:deviceId',
    component: DeviceEditComponent
  },
  {
    path: 'common',
    component: CommonComponent,
  },
  {
    path: 'construction',
    component: ConstructionComponent
  }
];
