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
import {BlockStraightBuilder} from "./control-center/track/track-viewer-svg/track-builder/component/block-straight";
import {TrackComponentBuilder} from "./control-center/track/track-viewer-svg/track-builder/track-component-builder";
import {CurveBuilder} from "./control-center/track/track-viewer-svg/track-builder/component/curve";
import {TurnoutBuilder} from "./control-center/track/track-viewer-svg/track-builder/component/turnout";
import {SignalBuilder} from "./control-center/track/track-viewer-svg/track-builder/component/signal";
import {StraightBuilder} from "./control-center/track/track-viewer-svg/track-builder/component/straight";
import {DeviceService} from "./shared/device.service";
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
    SnackBar,
    TrackComponentBuilder,
    BlockStraightBuilder,
    CurveBuilder,
    TurnoutBuilder,
    SignalBuilder,
    StraightBuilder,
    provideHttpClient(),
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true },
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync(), provideAnimationsAsync(),
    importProvidersFrom([ApiModule.forRoot(apiConfigFactory)])
  ]
}
