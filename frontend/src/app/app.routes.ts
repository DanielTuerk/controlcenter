import {Routes} from "@angular/router";

import {routes as ccRoutes} from "./control-center/control-center.routes"
import {WelcomeComponent} from "./welcome/welcome.component";
import {ControlCenterComponent} from "./control-center/control-center.component";
import {NotfoundComponent} from "./notfound/notfound.component";
import {StationComponent} from "./control-center/station/station.component";

export const routes:Routes = [
  {
    path: '',
    redirectTo: 'welcome',
    pathMatch: 'full',
  },
  {
    path: 'welcome',
    component: WelcomeComponent
  },
  {
    /*
     rendered without Header/Footer (bypasses ControlCenterComponent) so it can be popped
      out into its own browser window. Path is nested under 'cc' (and listed before the
     'cc' route below) so ConstructionService.updateCurrentConstruction's
     `router.url.startsWith('/cc')` check doesn't force-navigate it back to '/cc/viewer'.
     */
    path: 'cc/station-board-popout',
    component: StationComponent
  },
  {
    path: 'cc',
    component: ControlCenterComponent,
    children: ccRoutes,
  },
  {
    path: '**',
    component: NotfoundComponent
  }

];
