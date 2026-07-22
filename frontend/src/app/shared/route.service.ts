import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {Route, Track} from "../../shared/openapi-gen";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class RouteService {

  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);

  loadRoutes(): Observable<Route[]> {
    return this.httpClient.get<Route[]>('/api/routes')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load routes: ${err.message}`);
        return EMPTY
      })
    )
  }

  loadRoute(routeId: Number) {
    return this.httpClient.get<Route>('/api/routes/' + routeId)
  }

  createRoute(route: Route) {
    return this.httpClient.post('/api/routes/', route)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't create route: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveRoute(route: Route) {
    return this.httpClient.put('/api/routes/' + route.id, route)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't save route ${route.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  buildTrack(route: Route) {
    return this.httpClient.post<Track>('/api/routes/build-track', route)
  }

  deleteRoute(routeId: Number) {
    return this.httpClient.delete('/api/routes/' + routeId)
    .subscribe({
      next: (route) => {
        this.snackBar.showSuccess(`route ${routeId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete route ${routeId}: ${error.message}`);
      }
    });
  }
}
