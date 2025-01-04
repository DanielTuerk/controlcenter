import {inject} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";

export class SnackBar {
  private _snackBar = inject(MatSnackBar);

  showError(msg: string) {
    this.open(msg);
  }

  showSuccess(msg: string) {
    this.open(msg, 'Got It!', 5);
  }

  showMessage(msg: string) {
    this.open(msg, 'Got It!', 5);
  }

  private open(msg: string, action: string = 'Got It!', durationInSeconds: number = 5) {
    this._snackBar.open(msg, action, {
      duration: durationInSeconds * 1000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }
}
