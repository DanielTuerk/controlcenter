import {Component} from '@angular/core';
import {DeviceComponent} from "./device/device.component";
import {BusComponent} from "./bus/bus.component";
import {PlayerComponent} from "./player/player.component";

@Component({
  selector: 'app-footer',
  imports: [
    DeviceComponent,
    BusComponent,
    PlayerComponent
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
