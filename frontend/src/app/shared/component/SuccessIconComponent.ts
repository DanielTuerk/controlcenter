import {Component} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-success-icon',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <mat-icon style="color: #3f7707;">check</mat-icon>`
})
export class SuccessIconComponent {
}
