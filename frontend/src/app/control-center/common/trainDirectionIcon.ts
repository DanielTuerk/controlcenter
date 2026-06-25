import {Component, input, computed, ChangeDetectionStrategy} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'train-direction-icon',
  standalone: true,
  imports: [MatIconModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  styles: [`:host { display: flex; align-items: center; }`],
  template: `
    @if (icon()) {
      <mat-icon class="icon">{{ icon() }}</mat-icon>
    }
  `
})
export class TrainDirectionIcon {
  forward = input<boolean | undefined>();

  icon = computed(() => this.forward() ? 'arrow_forward' : 'arrow_backward');
}
