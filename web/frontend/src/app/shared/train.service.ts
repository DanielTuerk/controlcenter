import {inject, Injectable, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {DefaultService, Train} from "../../shared/openapi-gen";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class TrainService {

  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);
  private api = inject(DefaultService);
  private trains = signal<Train[]>([]);

  loadedTrains = this.trains.asReadonly();

  // constructor(private api: DefaultService) {}

  loadTrains(): Observable<Train[]> {
    return this.api.loadTrains()
  }

  loadTrain(trainId: Number) {
    return this.httpClient.get<Train>('/api/train/' + trainId)
  }

  createTrain(train: Train) {
    return this.httpClient.post('/api/train/', train)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't create train: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveTrain(train: Train) {
    return this.httpClient.post('/api/train/' + train.id, train)
    .pipe(
      catchError((err: any, caught: Observable<any>) => {
        this.snackBar.showError(`can't save train ${train.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteTrain(trainId: Number) {
    return this.httpClient.delete('/api/train/' + trainId)
    .subscribe({
      next: (train) => {
        this.snackBar.showSuccess(`train ${trainId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete train ${trainId}: ${error.message}`);
      }
    });
  }
}
