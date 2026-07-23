import {ChangeDetectionStrategy, Component, computed, effect, inject} from '@angular/core';
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {DeviceSubscription} from "../../../shared/websocket/device.subscription";
import {BusSubscription} from "../../../shared/websocket/bus.subscription";
import {BusService} from "../../../shared/bus.service";
import {SnackBar} from "../../common/snack-bar.component";
import {PLAYERSTATE, RECORDINGSTATE} from "../../../../shared/openapi-gen";
import {PlayerDialogComponent, PlayerDialogResult} from "./player-dialog/player-dialog.component";

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
  private busSubscription = inject(BusSubscription);
  private busService = inject(BusService);
  private snackBar = inject(SnackBar);
  private dialog = inject(MatDialog);

  isConnected = inject(DeviceSubscription).isConnected;

  isRecording = computed(() => this.busSubscription.recordingState()?.state === RECORDINGSTATE.Start);
  isPlaying = computed(() => this.busSubscription.playerState()?.state === PLAYERSTATE.Start);

  constructor() {
    effect(() => {
      const recording = this.busSubscription.recordingState();
      if (!recording) {
        return;
      }
      if (recording.state === RECORDINGSTATE.Start) {
        this.snackBar.showMessage('recording started');
      } else {
        this.snackBar.showMessage('recording stopped');
      }
    });

    effect(() => {
      const player = this.busSubscription.playerState();
      if (!player) {
        return;
      }
      if (player.state === PLAYERSTATE.Start) {
        this.snackBar.showMessage('playback started');
      } else {
        this.snackBar.showMessage('playback stopped');
      }
    });
  }

  onRecordingToggle() {
    if (this.isRecording()) {
      this.busService.stopRecording();
    } else {
      this.busService.startRecording();
    }
  }

  onPlayToggle() {
    if (this.isPlaying()) {
      this.busService.stopPlayer();
      return;
    }

    const dialogRef = this.dialog.open(PlayerDialogComponent);
    dialogRef.afterClosed().subscribe((result?: PlayerDialogResult) => {
      if (result) {
        this.busService.startPlayer(result.record, result.playbackSpeed);
      }
    });
  }
}
