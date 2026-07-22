import {Component, ChangeDetectionStrategy} from '@angular/core';
import {DeviceComponent} from "./device/device.component";
import {BusComponent} from "./bus/bus.component";
import {PlayerComponent} from "./player/player.component";
import {MatDivider} from "@angular/material/list";

@Component({
  selector: 'app-footer',
  imports: [
    DeviceComponent,
    BusComponent,
    PlayerComponent,
    MatDivider
  ],
  templateUrl: './footer.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
