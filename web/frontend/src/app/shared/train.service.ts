import {inject, Injectable, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {DefaultService, Train} from "../../shared/openapi-gen";
import {Observable} from "rxjs";

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

  // loadTrains() {
  //   this.httpClient.get<Train[]>('/api/train')
  //   .subscribe({
  //     next: (trains) => {
  //       console.log(trains);
  //       this.trains.set(trains);
  //     },
  //     error: (error) => {
  //       console.error(error)
  //     },
  //     complete: () => {
  //       console.log('done')
  //     }
  //   })
  // }

  loadTrain(trainId: Number) {
    // let trainWritableSignal = signal<Train|null>(null);
    // let foo = output<Train>();
    return this.httpClient.get<Train>('/api/train/' + trainId)
    // .subscribe({
    //   // next: (train) => {
    //   //   console.log(train);
    //   //   // return train;
    //   //   // trainWritableSignal.set(train)
    //   //   // foo.emit(train);
    //   // },
    //   error: (error) => {
    //     console.error(error)
    //   },
    //   complete: () => {
    //     console.log('done')
    //   }
    // });
    // return trainWritableSignal.asReadonly();
    // return foo;
  }

  deleteTrain(trainId: Number) {
    // TODO notifications
    this.httpClient.delete<Train>('/api/train/' + trainId)
    .subscribe({
      next: (train) => {
        console.log(train);
        this.snackBar.showSuccess('fooo successfully');
      },
      error: (error) => {
        console.error(error);
        this.snackBar.showError('fooo error');
      },
      complete: () => {
        console.log('done');
        this.snackBar.showMessage('fooo mess');
      }
    });
  }
}
