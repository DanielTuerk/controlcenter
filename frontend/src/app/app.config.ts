import {provideHttpClient} from "@angular/common/http";
import {provideRouter, withComponentInputBinding} from "@angular/router";
import {routes} from "./app.routes";
import {ApplicationConfig} from "@angular/core";
import {ConstructionService} from "./shared/construction.service";
import {TrainService} from "./shared/train.service";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [
    ConstructionService,
    TrainService,
    provideHttpClient(),
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync()
  ]
}
