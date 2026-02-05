import {Component} from '@angular/core';
import {TrackViewerSvgComponent} from "../track/track-viewer-svg/track-viewer-svg.component";
import {ControlComponent} from "./control/control.component";
import {TrackElement} from "../track/track-viewer-svg/track-element";

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

  protected onTrackPartClicked($trackEvent: TrackElement) {
    switch ($trackEvent.trackPart.trackPartType) {
      case 'Turnout':
        console.log("switch");
        // this.buildRouteOnTrack();
        break;
      case 'Signal':
        console.log("signal");
        break;
    }
  }
}
