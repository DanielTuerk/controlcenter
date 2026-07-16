import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {RUNSTATE} from "../../../shared/openapi-gen";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-scenario-run-state-icon',
  standalone: true,
  imports: [MatIconModule, MatTooltip],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `
    @if (runState()) {
      <mat-icon
        [matTooltip]="runState()"
        style="font-size: 18px;  width: 18px;  height: 18px;
      color: #f1ebeb; margin-right: 2pt">{{ iconForState() }}
      </mat-icon>
    }
  `
})
export class ScenarioRunStateIconComponent {
  public runState = input<RUNSTATE | null>(null)

  protected iconForState() {
    switch (this.runState()) {
      case RUNSTATE.None:
        return '';
      case RUNSTATE.Scheduled:
        return 'timer';
      case RUNSTATE.Running:
        return 'change_circle';
      case RUNSTATE.Paused:
        return 'do_not_disturb_on';
      case RUNSTATE.Success:
        return 'check_circle';
      case RUNSTATE.Stopped:
        return 'cancel';
      case RUNSTATE.Failed:
        return 'error';
      default:
        return '';
    }
  }
}
