import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {MatButton} from "@angular/material/button";
import {BusService} from "../../../../shared/bus.service";

export interface PlayerDialogResult {
  record: string;
  playbackSpeed: number;
}

@Component({
  selector: 'app-player-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatSlider,
    MatSliderThumb,
    MatButton
  ],
  templateUrl: './player-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './player-dialog.component.css'
})
export class PlayerDialogComponent implements OnInit {
  private busService = inject(BusService);
  private dialogRef = inject(MatDialogRef<PlayerDialogComponent>);

  readonly records = signal<string[]>([]);
  readonly playbackSpeed = signal(1);

  ngOnInit() {
    this.busService.listRecords().subscribe(records => this.records.set(records));
  }

  onSelectRecord(record: string): void {
    this.dialogRef.close({record, playbackSpeed: this.playbackSpeed()} as PlayerDialogResult);
  }
}
