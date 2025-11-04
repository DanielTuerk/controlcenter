import {Component} from '@angular/core';
import {TrackViewerSvgComponent} from "../track/track-viewer-svg/track-viewer-svg.component";

@Component({
  selector: 'app-viewer',
  imports: [
    TrackViewerSvgComponent
  ],
  templateUrl: './viewer.component.html',
  styleUrl: './viewer.component.css'
})
export class ViewerComponent {

}
