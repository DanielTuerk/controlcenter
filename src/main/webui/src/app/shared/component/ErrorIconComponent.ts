import {Component} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-error-icon',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <mat-icon style="color:#b34444;">error</mat-icon>`
})
export class ErrorIconComponent {
}
