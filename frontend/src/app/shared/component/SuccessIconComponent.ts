import {Component, ChangeDetectionStrategy} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-success-icon',
  standalone: true,
  imports: [MatIconModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `
    <mat-icon style="color: #3f7707;">check</mat-icon>`
})
export class SuccessIconComponent {
}
