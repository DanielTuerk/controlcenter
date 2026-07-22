import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-event-dump-list-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatButton
  ],
  templateUrl: './event-dump-list-dialog.component.html',
  styleUrl: './event-dump-list-dialog.component.css',
})
export class EventDumpListDialogComponent {

  constructor(public dialogRef: MatDialogRef<EventDumpListDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public fileNames: string[]) {
  }

  protected select(fileName: string) {
    this.dialogRef.close(fileName);
  }
}
