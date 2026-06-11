import {inject, Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";
import {DRIVINGDIRECTION, Train} from "../../shared/openapi-gen";

@Injectable({
  providedIn: 'root',
})
export class TrainService {

  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);

  loadTrains(): Observable<Train[]> {
    return this.httpClient.get<Train[]>('/api/trains')
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't load trains: ${err.error}`);
        return EMPTY
      })
    )
  }

  loadTrain(trainId: Number) {
    return this.httpClient.get<Train>('/api/trains/' + trainId)
  }

  createTrain(train: Train) {
    return this.httpClient.post('/api/trains/', train)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't create train: ${err.error}`);
        return EMPTY
      })
    )
  }

  saveTrain(train: Train) {
    return this.httpClient.put('/api/trains/' + train.id, train)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't save train ${train.id}: ${err.error}`);
        return EMPTY
      })
    )
  }

  deleteTrain(trainId: Number) {
    return this.httpClient.delete('/api/trains/' + trainId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`train ${trainId} deleted`);
      },
      error: (error:HttpErrorResponse) => {
        this.snackBar.showError(`can't delete train ${trainId}: ${error.error}`);
      }
    });
  }

  toggleDirection(train: Train, direction: DRIVINGDIRECTION) {
    return this.httpClient.post(`/api/trains/${train.id}/direction`, `${direction}`)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't change direction of train: ${err.error}`);
        return EMPTY
      })
    )
  }

  changeDrivingLevel(train: Train, level: number) {
    return this.httpClient.post(`/api/trains/${train.id}/level`, `${level}`)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't change level of train: ${err.error}`);
        return EMPTY
      })
    )
  }

  toggleLight(train: Train, state: boolean) {
    return this.httpClient.post(`/api/trains/${train.id}/light`, `${state}`)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't change light of train: ${err.error}`);
        return EMPTY
      })
    )
  }

  toggleHorn(train: Train, state: boolean) {
    return this.httpClient.post(`/api/trains/${train.id}/horn`, `${state}`)
    .pipe(
      catchError((err: HttpErrorResponse) => {
        this.snackBar.showError(`can't change horn of train: ${err.error}`);
        return EMPTY
      })
    )
  }
}
