import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";

@Component({
  selector: 'app-player',
  imports: [
    MatIconButton,
    MatIcon,
    MatTooltip
  ],
  templateUrl: './player.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './player.component.css'
})
export class PlayerComponent {
  isConnected = inject(DeviceSubscription).isConnected;

  onRecordingToggle() {
    // TODO
    console.log("TODO: no recording");
  }
}
