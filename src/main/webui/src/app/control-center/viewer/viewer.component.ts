import {Component, inject} from '@angular/core';
import {TrackViewerSvgComponent} from "../track/track-viewer-svg/track-viewer-svg.component";
import {ControlComponent} from "./control/control.component";
import {TrackElement} from "../track/track-viewer-svg/track-element";
import {TrackService} from "../../shared/track.service";

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

  protected onTrackPartClicked($trackEvent: TrackElement) {
    if ('toggleFunction' in $trackEvent.trackPart) {
      console.log("toggle " + $trackEvent.trackPart.trackPartType)
      this.trackService.toggleTrackPart($trackEvent.trackPart)
      .subscribe();
    }
    // TODO signal
    // switch ($trackEvent.trackPart.trackPartType) {
    //   case 'Signal':
    //     console.log("signal");
    //     break;
    // }
  }
}
