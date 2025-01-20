import {provideHttpClient} from "@angular/common/http";
import {provideRouter, withComponentInputBinding} from "@angular/router";
import {routes} from "./app.routes";
import {ApplicationConfig, importProvidersFrom} from "@angular/core";
import {ConstructionService} from "./shared/construction.service";
import {TrainService} from "./shared/train.service";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {SnackBar} from "./control-center/common/snack-bar.component";
import {ApiModule, Configuration, ConfigurationParameters} from "../shared/openapi-gen";
import {environment} from "../env/local.env";

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.API_BASE_PATH,
  };
  return new Configuration(params);
}

export const appConfig: ApplicationConfig = {
  providers: [
    ConstructionService,
    TrainService,
    SnackBar,
    provideHttpClient(),
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync(), provideAnimationsAsync(),
    importProvidersFrom([ApiModule.forRoot(apiConfigFactory)])
  ]
}
