import {Component, inject} from '@angular/core';
import {TrackViewerSvgComponent} from "../track/track-viewer-svg/track-viewer-svg.component";
import {ControlComponent} from "./control/control.component";
import {TrackElement} from "../track/track-viewer-svg/track-element";
import {TrackService} from "../../shared/track.service";
import {FUNCTION, Signal, SIGNALTYPE} from "../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {SelectSignalFunctionComponent} from "./select-signal-func/select-signal-func.component";

@Component({
  selector: 'app-viewer',
  imports: [
    TrackViewerSvgComponent,
    ControlComponent
  ],
  templateUrl: './viewer.component.html',
  styleUrl: './viewer.component.css'
})
export class ViewerComponent {

  private trackService = inject(TrackService);
  readonly selectSignalFuncDialog = inject(MatDialog);

  protected onTrackPartClicked($trackElement: TrackElement<any>) {
    if ('toggleFunction' in $trackElement.trackPart) {
      console.log("toggle " + $trackElement.trackPart.trackPartType);
      this.trackService.toggleTrackPart($trackElement.trackPart)
      .subscribe();
    } else if ($trackElement.trackPart.trackPartType === 'Signal') {
      console.log(`switch signal ${$trackElement}`);

      let signal: Signal = <Signal>$trackElement.trackPart;

      let newFuncState = FUNCTION.Hp0;
      switch (signal.type) {
        case SIGNALTYPE.Block:
        case SIGNALTYPE.Before:
          if ($trackElement.lastEvent && 'signalFunction' in $trackElement.lastEvent) {
            if ($trackElement.lastEvent.signalFunction === FUNCTION.Hp0) {
              newFuncState = FUNCTION.Hp1;
            }
          }
          this.trackService.switchSignal($trackElement.trackPart, newFuncState).subscribe();
          break;
        case SIGNALTYPE.Enter:
          this.selectSignalFuncDialog.open(SelectSignalFunctionComponent, {
            data: [FUNCTION.Hp0, FUNCTION.Hp1, FUNCTION.Hp2]
          }).afterClosed().subscribe((func) => {
            if (func) {
              this.trackService.switchSignal($trackElement.trackPart, func).subscribe();
            }
          });
          break;
        case SIGNALTYPE.Exit:
          this.selectSignalFuncDialog.open(SelectSignalFunctionComponent, {
            data: [FUNCTION.Hp0, FUNCTION.Hp1, FUNCTION.Hp2, FUNCTION.Hp0Sh1]
          }).afterClosed().subscribe((func) => {
            if (func) {
              this.trackService.switchSignal($trackElement.trackPart, func).subscribe();
            }
          });
          break;
        default:
          throw Error(`invalid signal type: ${signal.type}`)
      }
    }

  }
}
