import {Component, Inject, ChangeDetectionStrategy} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {BusDataDto} from "../../../../../shared/openapi-gen";

@Component({
  selector: 'app-bus-send-data-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormField,
    MatLabel,
    MatDialogActions,
    MatButton,
    MatInput,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './bus-send-data-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './bus-send-data-dialog.component.css'
})
export class BusSendDataDialogComponent {
  constructor(public dialogRef: MatDialogRef<BusSendDataDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: BusDataDto,
              private fb: FormBuilder) {
  }

  form = this.fb.group({
    bus: [0, [Validators.required, Validators.min(0), Validators.max(1)]],
    address: [1, [Validators.required, Validators.min(1), Validators.max(111)]],
    value: [0, [Validators.required, Validators.min(0), Validators.max(255)]]
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  onSend(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value);
  }
}
