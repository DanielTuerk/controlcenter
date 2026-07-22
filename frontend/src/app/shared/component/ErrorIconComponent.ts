import {Component, ChangeDetectionStrategy} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-error-icon',
  standalone: true,
  imports: [MatIconModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `
    <mat-icon style="color:#b34444;">error</mat-icon>`
})
export class ErrorIconComponent {
}
