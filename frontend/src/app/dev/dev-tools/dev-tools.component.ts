import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {HttpClient} from "@angular/common/http";
import {SnackBar} from "../../control-center/common/snack-bar.component";

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

  protected createBusDataDump() {
    this.httpClient.get<string>('/api/devtool/bus-dump').subscribe({
      next: data => {
        this.snackBar.showMessage('bus data dump created: ' + data);
      },
      error: err => {
        console.error('Could not create bus data dump', err);
        this.snackBar.showMessage('bus data dump failed');
      }
    });
  }
}
