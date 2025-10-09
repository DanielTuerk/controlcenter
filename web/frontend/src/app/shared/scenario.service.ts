import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {DefaultService, Scenario} from "../../shared/openapi-gen";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class ScenarioService {

  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);
  private api = inject(DefaultService);

  loadScenarios(): Observable<Scenario[]> {
    return this.api.loadScenarios().pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load scenarios: ${err.message}`);
        return EMPTY
      })
    )
  }

  loadScenario(scenarioId: Number) {
    return this.httpClient.get<Scenario>('/api/scenario/' + scenarioId)
  }

  createScenario(scenario: Scenario) {
    return this.httpClient.post('/api/scenario/', scenario)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't create scenario: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveScenario(scenario: Scenario) {
    return this.httpClient.post('/api/scenario/' + scenario.id, scenario)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't save scenario ${scenario.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteScenario(scenarioId: Number) {
    return this.httpClient.delete('/api/scenario/' + scenarioId)
    .subscribe({
      next: (scenario) => {
        this.snackBar.showSuccess(`scenario ${scenarioId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete scenario ${scenarioId}: ${error.message}`);
      }
    });
  }
}
