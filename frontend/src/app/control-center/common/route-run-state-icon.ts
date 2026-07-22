import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {ROUTERUNSTATE} from "../../../shared/openapi-gen";

@Component({
  selector: 'app-route-run-state-icon',
  standalone: true,
  imports: [MatIconModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `
    @if (runState()) {
      <mat-icon style="color: #f1ebeb; margin-right: 2pt">{{ iconForState() }}</mat-icon>
    }
  `
})
export class RouteRunStateIconComponent {
  public runState = input<ROUTERUNSTATE | null>(null)

  protected iconForState() {
    switch (this.runState()) {
      case ROUTERUNSTATE.Prepared:
        return 'hourglass';
      case ROUTERUNSTATE.Reserved:
        return 'timer';
      case ROUTERUNSTATE.Running:
        return 'change_circle';
      case ROUTERUNSTATE.Finished:
        return 'check_circle';
      case ROUTERUNSTATE.Skipped:
        return 'do_not_disturb_on';
      case ROUTERUNSTATE.Failed:
        return 'error';
      case ROUTERUNSTATE.Canceled:
        return 'cancel';
      default:
        return '';
    }
  }
}
