import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {HttpClient} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {SnackBar} from "../common/snack-bar.component";
import {BusDumpListDialogComponent} from "./bus-dump-list-dialog/bus-dump-list-dialog.component";
import {EventDumpListDialogComponent} from "./event-dump-list-dialog/event-dump-list-dialog.component";

@Component({
  selector: 'app-dev-tools',
  imports: [
    MatButton
  ],
  templateUrl: './dev-tools.component.html',
  styleUrl: './dev-tools.component.css',
})
export class DevToolsComponent {

  private httpClient = inject(HttpClient);
  private snackBar = inject(SnackBar);
  private dialog = inject(MatDialog);

  protected createBusDataDump() {
    this.httpClient.get('/api/devtool/bus-dump', {responseType: 'text'}).subscribe({
      next: data => {
        this.snackBar.showMessage('bus data dump created: ' + data);
      },
      error: err => {
        console.error('Could not create bus data dump', err);
        this.snackBar.showError('bus data dump failed');
      }
    });
  }

  protected openBusDumpListDialog() {
    this.httpClient.get<string[]>('/api/devtool/bus-dump/list').subscribe({
      next: fileNames => {
        const dialogRef = this.dialog.open(BusDumpListDialogComponent, {
          data: fileNames
        });
        dialogRef.afterClosed().subscribe(fileName => {
          if (fileName) {
            this.httpClient.post(`/api/devtool/bus-dump/load/${encodeURIComponent(fileName)}`, null).subscribe({
              error: err => {
                console.error('Could not load bus data dump', err);
                this.snackBar.showError('load bus data dump failed');
              }
            });
          }
        });
      },
      error: err => {
        console.error('Could not list bus data dumps', err);
        this.snackBar.showError('could not list bus data dumps');
      }
    });
  }

  protected createEventDump() {
    this.httpClient.get('/api/devtool/event-dump', {responseType: 'text'}).subscribe({
      next: data => {
        this.snackBar.showMessage('event dump created: ' + data);
      },
      error: err => {
        console.error('Could not create event dump', err);
        this.snackBar.showError('event dump failed');
      }
    });
  }

  protected openEventDumpListDialog() {
    this.httpClient.get<string[]>('/api/devtool/event-dump/list').subscribe({
      next: fileNames => {
        const dialogRef = this.dialog.open(EventDumpListDialogComponent, {
          data: fileNames
        });
        dialogRef.afterClosed().subscribe(fileName => {
          if (fileName) {
            this.httpClient.post(`/api/devtool/event-dump/load/${encodeURIComponent(fileName)}`, null).subscribe({
              error: err => {
                console.error('Could not load event dump', err);
                this.snackBar.showError('load event dump failed');
              }
            });
          }
        });
      },
      error: err => {
        console.error('Could not list event dumps', err);
        this.snackBar.showError('could not list event dumps');
      }
    });
  }
}
