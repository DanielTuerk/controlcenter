import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {BusBitDto, BusDataDto, SYSTEMFORMAT} from "../../shared/openapi-gen";

@Injectable({providedIn: 'root'})
export class BusService {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);

  toggleRailVoltage() {
    return this.callBusAction('railvoltage')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't toggle railvoltage: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  switchSystemFormat() {
    return this.callBusAction('system-format')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't switch system format: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  currentSystemFormat() {
    return this.httpClient.get<SYSTEMFORMAT>('/api/bus/system-format')
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't fetch system format: ${err.message}`);
          return EMPTY
        })
      );
  }

  sendBusBit(busBit: BusBitDto) {
    return this.callBusAction('bus-bit', busBit)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't send state of bus bit (${busBit}): ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  sendBusData(busData:BusDataDto) {
    return this.callBusAction('bus-data', busData)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't send bus data (${busData}): ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  startTrackingBus(connectionId: string) {
    return this.callBusAction('start-tracking-bus', {clientId: connectionId})
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't start to track bus: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  stopTrackingBus(connectionId: string) {
    return this.callBusAction('stop-tracking-bus', {clientId: connectionId})
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't stop to track bus: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  startRecording() {
    return this.callBusAction('start-recording-bus')
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't start recording: ${err.message}`);
          return EMPTY
        })
      ).subscribe();
  }

  stopRecording() {
    return this.callBusAction('stop-recording-bus')
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't stop recording: ${err.message}`);
        return EMPTY
      })
    ).subscribe();
  }

  startPlayer(record: string, playbackSpeed: number) {
    return this.httpClient.post<null>('/api/bus/start-bus-player', null, {
      params: {record, playbackSpeed}
    })
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't start player: ${err.message}`);
          return EMPTY
        })
      ).subscribe();
  }

  stopPlayer() {
    return this.callBusAction('stop-bus-player')
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't stop player: ${err.message}`);
          return EMPTY
        })
      ).subscribe();
  }

  listRecords() {
    return this.httpClient.get<string[]>('/api/bus/playback/list')
      .pipe(
        catchError((err: any) => {
          this.snackBar.showError(`can't fetch records: ${err.message}`);
          return EMPTY
        })
      );
  }

  private callBusAction(path: string, body:any=null) {
    return this.httpClient.post<null>('/api/bus/' + path, body)
  }
}
