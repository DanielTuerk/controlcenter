import {Component, Input} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
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
  @Input() forward: boolean | undefined;

  get icon() {
    return this.forward ? 'arrow_forward' : 'arrow_backward';
  }
}
