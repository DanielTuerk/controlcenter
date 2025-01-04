import {provideHttpClient} from "@angular/common/http";
import {provideRouter, withComponentInputBinding} from "@angular/router";
import {routes} from "./app.routes";
import {ApplicationConfig} from "@angular/core";
import {ConstructionService} from "./shared/construction.service";
import {TrainService} from "./shared/train.service";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {SnackBar} from "./control-center/common/snack-bar.component";

export const appConfig: ApplicationConfig = {
  providers: [
    ConstructionService,
    TrainService,
    SnackBar,
    provideHttpClient(),
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync(), provideAnimationsAsync()
  ]
}
