import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {AbstractTrackPart, FUNCTION, TrackBlock, Train} from "../../shared/openapi-gen";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class TrackService {
  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);

  fetchTrackParts() {
    return this.httpClient.get<[]>('/api/track')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load track: ${err.message}`);
        return EMPTY
      })
    )
  }
  toggleTrackPart(trackPart: AbstractTrackPart) {
    return this.httpClient.post(`/api/track/${trackPart.id}/toggle`, null)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't toggle track part (${trackPart.id}): ${err.message}`);
        return EMPTY
      })
    )
  }

  switchSignal(trackPart: AbstractTrackPart, signalFunc: FUNCTION) {
    return this.httpClient.post(`/api/track/${trackPart.id}/switch-signal`, signalFunc)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't switch signal (${trackPart.id}): ${err.message}`);
        return EMPTY
      })
    )
  }

  loadTrackBlocks() {
    return this.httpClient.get<[]>('/api/track-block')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load track-block: ${err.message}`);
        return EMPTY
      })
    )
  }

  loadTrackBlock(trackBlockId: Number) {
    return this.httpClient.get<Train>('/api/track-block/' + trackBlockId)
  }

  createTrackBlock(trackBlock: TrackBlock) {
    return this.httpClient.post('/api/track-block/', trackBlock)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't create track-block: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveTrackBlock(train: Train) {
    return this.httpClient.put('/api/track-block/' + train.id, train)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save track-block ${train.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteTrackBlock(trackBlockId: Number) {
    return this.httpClient.delete('/api/track-block/' + trackBlockId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`track-block ${trackBlockId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete track-block ${trackBlockId}: ${error.message}`);
      }
    });
  }
}
