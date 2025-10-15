import {inject} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";

export class SnackBar {
  private _snackBar = inject(MatSnackBar);

  showError(msg: string) {
    console.error(msg)
    this.open(msg, 'error');
  }

  showSuccess(msg: string) {
    this.open(msg, 'success', 5);
  }

  showMessage(msg: string) {
    this.open(msg);
  }

  private open(msg: string, type:string = '', durationInSeconds: number = 10) {
    this._snackBar.open(msg, 'Got It!', {
      duration: durationInSeconds * 1000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['snackbar-'+type]
    });
  }
}
