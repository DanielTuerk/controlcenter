import {HTTP_INTERCEPTORS, provideHttpClient} from "@angular/common/http";
import {provideRouter, withComponentInputBinding} from "@angular/router";
import {routes} from "./app.routes";
import {ApplicationConfig, importProvidersFrom} from "@angular/core";
import {ConstructionService} from "./shared/construction.service";
import {TrainService} from "./shared/train.service";
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {SnackBar} from "./control-center/common/snack-bar.component";
import {ApiModule, Configuration, ConfigurationParameters} from "../shared/openapi-gen";
import {environment} from "../env/local.env";
import {HttpErrorInterceptor} from "./http-error.interceptor";
import {TrackComponentBuilder} from "./control-center/track/track-viewer-svg/track-builder/track-component-builder";
import {DeviceService} from "./shared/device.service";
import {ConfigService} from "./shared/config.service";
import {BusService} from "./shared/bus.service";

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.API_BASE_PATH,
  };
  return new Configuration(params);
}

export const appConfig: ApplicationConfig = {
  providers: [
    DeviceService,
    BusService,
    ConstructionService,
    TrainService,
    ConfigService,
    SnackBar,
    TrackComponentBuilder,
    provideHttpClient(),
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true },
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync(), provideAnimationsAsync(),
    importProvidersFrom([ApiModule.forRoot(apiConfigFactory)])
  ]
}
