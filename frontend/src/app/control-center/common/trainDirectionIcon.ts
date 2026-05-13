import {Component, Input} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {NgIf} from "@angular/common";
import {TrainData} from "../viewer/control/train/train.component";

@Component({
  selector: 'train-direction-icon',
  standalone: true,
  imports: [MatIconModule, NgIf],
  template: `
    <mat-icon class="icon" *ngIf="icon">{{ icon }}</mat-icon>
  `
})
export class TrainDirectionIcon {
  @Input() trainData: TrainData | undefined;

  get icon() {
    if (this.trainData) {
      return this.trainData.forward ? 'arrow_forward' : 'arrow_backward';
    }
    return null;
  }
}
