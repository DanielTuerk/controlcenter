import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {Scenario, ScenarioStatistic} from "../../shared/openapi-gen";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";
import {ScenarioData} from "../control-center/viewer/control/scenario/scenario.component";

@Injectable({
  providedIn: 'root',
})
export class ScenarioService {

  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);

  loadScenarios(): Observable<Scenario[]> {
    return this.httpClient.get<Scenario[]>('/api/scenarios')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load scenarios: ${err.message}`);
        return EMPTY
      })
    )
  }

  loadScenario(scenarioId: Number) {
    return this.httpClient.get<Scenario>(`/api/scenarios/${scenarioId}`)
  }

  loadScenarioStatistic(scenarioId: Number) {
    return this.httpClient.get<ScenarioStatistic>(`/api/scenarios/${scenarioId}/statistic`)
  }

  createScenario(scenario: Scenario) {
    return this.httpClient.post('/api/scenarios/', scenario)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't create scenario: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveScenario(scenario: Scenario) {
    return this.httpClient.put(`/api/scenarios/${scenario.id}`, scenario)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save scenario ${scenario.name}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteScenario(scenarioId: Number) {
    return this.httpClient.delete('/api/scenarios/' + scenarioId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`scenario ${scenarioId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete scenario ${scenarioId}: ${error.message}`);
      }
    });
  }

  startScenario(scenario: ScenarioData) {
    return this.httpClient.post(`/api/scenarios/${scenario.scenarioId}/start`, null)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`scenario ${scenario.name} (${scenario.scenarioId}) started`);
      },
      error: (error) => {
        this.snackBar.showError(`can't start scenario ${scenario.name} (${scenario.scenarioId}): ${error.message}`);
      }
    });
  }

  scheduleScenario(scenario: ScenarioData) {
    return this.httpClient.post(`/api/scenarios/${scenario.scenarioId}/schedule`, null)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`scenario ${scenario.name} (${scenario.scenarioId}) scheduled`);
      },
      error: (error) => {
        this.snackBar.showError(`can't schedule scenario ${scenario.name} (${scenario.scenarioId}): ${error.message}`);
      }
    });
  }

  stopScenario(scenario: ScenarioData) {
    return this.httpClient.post(`/api/scenarios/${scenario.scenarioId}/cancel`, null)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`scenario ${scenario.name} (${scenario.scenarioId}) stopped`);
      },
      error: (error) => {
        this.snackBar.showError(`can't stop scenario ${scenario.name} (${scenario.scenarioId}): ${error.message}`);
      }
    });
  }

  scheduleAll() {
    return this.httpClient.post(`/api/scenarios/schedule-all`, null)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`all scenarios scheduled`);
      },
      error: (error) => {
        this.snackBar.showError(`can't schedule all scenarios: ${error.message}`);
      }
    });
  }

  stopAll() {
    return this.httpClient.post(`/api/scenarios/cancel-all`, null)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`all scenarios stopped`);
      },
      error: (error) => {
        this.snackBar.showError(`can't stop all scenarios: ${error.message}`);
      }
    });
  }

  unscheduleScenario(scenario: ScenarioData) {
    return this.httpClient.post(`/api/scenarios/${scenario.scenarioId}/unschedule`, null)
      .subscribe({
        next: () => {
          this.snackBar.showSuccess(`scenario ${scenario.name} (${scenario.scenarioId}) unscheduled`);
        },
        error: (error) => {
          this.snackBar.showError(`can't unschedule scenario ${scenario.name} (${scenario.scenarioId}): ${error.message}`);
        }
      });
  }

  unscheduleAll() {
    return this.httpClient.post(`/api/scenarios/unschedule-all`, null)
      .subscribe({
        next: () => {
          this.snackBar.showSuccess(`all scenarios unscheduled`);
        },
        error: (error) => {
          this.snackBar.showError(`can't unschedule all scenarios: ${error.message}`);
        }
      });
  }

}
