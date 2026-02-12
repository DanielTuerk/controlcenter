import {ChangeDetectionStrategy, Component, inject, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {FUNCTION} from "../../../../shared/openapi-gen";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-select-signal-func',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    FormsModule,
    NgForOf
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './select-signal-func.component.html',
  styleUrl: './select-signal-func.component.css'
})
export class SelectSignalFunctionComponent {
  readonly functions = signal<FUNCTION[]>(inject<FUNCTION[]>(MAT_DIALOG_DATA));

}
