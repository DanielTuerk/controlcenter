import {inject, Injectable} from "@angular/core";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {Station, StationDto} from "../../shared/openapi-gen";
import {EMPTY, Observable} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({providedIn: 'root'})
export class StationService {

  private snackBar = inject(SnackBar);
  private httpClient = inject(HttpClient);

  loadStations(): Observable<Station[]> {
    return this.httpClient.get<Station[]>('/api/stations')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load stations: ${err.message}`);
        return EMPTY
      })
    )
  }

  loadStation(stationId: Number) {
    return this.httpClient.get<Station>('/api/stations/' + stationId)
  }

  createStation(station: Station) {
    return this.httpClient.post('/api/stations/', this.toStationDto(station))
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't create station: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveStation(station: Station) {
    return this.httpClient.put('/api/stations/' + station.id, this.toStationDto(station))
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save station ${station.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteStation(stationId: Number) {
    return this.httpClient.delete('/api/stations/' + stationId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`station ${stationId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete station ${stationId}: ${error.message}`);
      }
    });
  }

  private toStationDto(station: Station): StationDto {
    return {
      name: station.name!,
      platforms: (station.platforms ?? []).map(platform => ({
        id: platform.id,
        name: platform.name!,
        trackBlockIds: (platform.trackBlocks ?? []).map(trackBlock => trackBlock.id!)
      }))
    };
  }
}
