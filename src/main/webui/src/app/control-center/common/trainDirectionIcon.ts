import {Component, Input} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {Train} from "../../../shared/openapi-gen";
import {NgIf} from "@angular/common";

@Component({
  selector: 'train-direction-icon',
  standalone: true,
  imports: [MatIconModule, NgIf],
  template: `
    <mat-icon class="icon" *ngIf="icon">{{ icon }}</mat-icon>
  `
})
export class TrainDirectionIcon {
  @Input() train: Train | undefined;

  get icon() {
    if (this.train) {
      return this.train.forward ? 'arrow_forward' : 'arrow_backward';
    }
    return null;
  }
}
