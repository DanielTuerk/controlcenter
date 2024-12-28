import {inject, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Train} from "./shared.model";

export class TrainService {

  private httpClient = inject(HttpClient);
  private trains = signal<Train[]>([]);

  loadedTrains = this.trains.asReadonly();

  loadTrains() {
    this.httpClient.get<Train[]>('/api/train')
    .subscribe({
      next: (trains) => {
        console.log(trains);
        this.trains.set(trains);
      },
      error: (error) => {
        console.error(error)
      },
      complete: () => {
        console.log('done')
      }
    })
  }

  loadTrain(trainId: Number) {
    let trainWritableSignal = signal<Train|null>(null);
    this.httpClient.get<Train>('/api/train/' + trainId)
    .subscribe({
      next: (train) => {
        console.log(train);
        trainWritableSignal.set(train)
      },
      error: (error) => {
        console.error(error)
      },
      complete: () => {
        console.log('done')
      }
    });
    return trainWritableSignal.asReadonly();
  }
}
