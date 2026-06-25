import {Component, Input, ChangeDetectionStrategy} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';


@Component({
  selector: 'train-direction-icon',
  standalone: true,
  imports: [MatIconModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  styles: [`:host { display: flex; align-items: center; }`],
  template: `
    @if (icon) {
      <mat-icon class="icon">{{ icon }}</mat-icon>
    }
    `
})
export class TrainDirectionIcon {
  @Input() forward: boolean | undefined;

  get icon() {
    return this.forward ? 'arrow_forward' : 'arrow_backward';
  }
}
