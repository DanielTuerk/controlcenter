import {Routes} from "@angular/router";

import {routes as ccRoutes} from "./control-center/control-center.routes"
import {WelcomeComponent} from "./welcome/welcome.component";
import {ControlCenterComponent} from "./control-center/control-center.component";
import {NotfoundComponent} from "./notfound/notfound.component";

export const routes:Routes = [
  {
    path: 'welcome',
    component: WelcomeComponent
  },
  {
    path: 'cc',
    component: ControlCenterComponent,
    children: ccRoutes
  },
  {
    path: '**',
    component: NotfoundComponent
  }

];
