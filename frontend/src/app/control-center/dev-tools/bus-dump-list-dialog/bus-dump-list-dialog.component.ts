import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-bus-dump-list-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatButton
  ],
  templateUrl: './bus-dump-list-dialog.component.html',
  styleUrl: './bus-dump-list-dialog.component.css',
})
export class BusDumpListDialogComponent {

  constructor(public dialogRef: MatDialogRef<BusDumpListDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public fileNames: string[]) {
  }

  protected select(fileName: string) {
    this.dialogRef.close(fileName);
  }
}
