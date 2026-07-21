import {HTTP_INTERCEPTORS, provideHttpClient, withXhr} from "@angular/common/http";
import {provideRouter, withComponentInputBinding} from "@angular/router";
import {routes} from "./app.routes";
import {ApplicationConfig, importProvidersFrom} from "@angular/core";
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";
import {ApiModule, Configuration, ConfigurationParameters} from "../shared/openapi-gen";
import {environment} from "../env/local.env";
import {HttpErrorInterceptor} from "./http-error.interceptor";

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.API_BASE_PATH,
  };
  return new Configuration(params);
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withXhr()),
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true },
    provideRouter(routes, withComponentInputBinding()), provideAnimationsAsync(),
    importProvidersFrom([ApiModule.forRoot(apiConfigFactory)]),
    { provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { subscriptSizing: 'dynamic' } }
  ]
}
