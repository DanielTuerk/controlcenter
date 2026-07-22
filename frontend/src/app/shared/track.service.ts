import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {AbstractTrackPart, ChangeTrackDto, FUNCTION, TrackBlock, Train} from "../../shared/openapi-gen";
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
    return this.httpClient.get<Train>(`/api/track-block/${trackBlockId}`)
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

  saveTrackBlock(trackBlock: TrackBlock) {
    return this.httpClient.put(`/api/track-block/${trackBlock.id}`, trackBlock)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save track-block ${trackBlock.id}: ${err.message}`);
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

  loadTrackPart(trackPartId: Number) {
    return this.httpClient.get<AbstractTrackPart>(`/api/track/${trackPartId}`)
  }

  saveTrackPart(toUpdate: AbstractTrackPart) {
    return this.httpClient.put(`/api/track/${toUpdate.id}`, toUpdate)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save track-part ${toUpdate.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteTrackPart(trackPartId: Number) {
    return this.httpClient.delete(`/api/track/${trackPartId}`)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`track-part ${trackPartId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete track-part ${trackPartId}: ${error.message}`);
      }
    });
  }

  changeTrack(changeTrackDto: ChangeTrackDto) {
    return this.httpClient.put(`/api/track`, changeTrackDto)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't change track ${changeTrackDto}: ${err.message}`);
        return EMPTY
      })
    )
  }

}
