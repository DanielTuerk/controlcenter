import {Component} from '@angular/core';
import {TrackViewerSvgComponent} from "../track/track-viewer-svg/track-viewer-svg.component";
import {ControlComponent} from "./control/control.component";

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

}
