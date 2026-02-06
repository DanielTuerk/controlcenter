import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {AbstractTrackPart} from "../../shared/openapi-gen";
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
}
