import {Component, inject, OnInit, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {TrainService} from "../../../../shared/train.service";
import {MatDivider} from "@angular/material/divider";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {NgForOf, NgIf} from "@angular/common";
import {TrainSubscription} from "../../../../shared/websocket/train.subscription";
import {DRIVINGDIRECTION, Train} from "../../../../../shared/openapi-gen";
import {RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DeviceService} from "../../../../shared/device.service";

export class TrainData {
  train: Train;
  horn: boolean;
  light: boolean;

  constructor(train: Train, horn: boolean, light: boolean) {
    this.train = train;
    this.horn = horn;
    this.light = light;
  }
}


@Component({
  selector: 'app-viewer-control-train',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatButtonToggleGroup,
    MatButtonToggle,
    MatIcon,
    MatMiniFabButton,
    MatDivider,
    MatSlider,
    MatSliderThumb,
    NgForOf,
    RouterLink,
    FormsModule,
    NgIf
  ],
  templateUrl: './train.component.html',
  styleUrl: './train.component.css'
})
export class TrainComponent implements OnInit {
  private trainService = inject(TrainService);
  private trainSubscription = inject(TrainSubscription);
  trains = signal<TrainData[]>([]);
  protected isConnected = inject(DeviceService).isConnected;

  ngOnInit() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data.map(train => {
        return new TrainData(train, false, false);
      }));

      this.trainSubscription.trainDataChanged().subscribe(() => {
        this.trainService.loadTrains().subscribe(updatedTrains => {

          let trainData = this.trains();
          let updatedTrainData: TrainData[] = [];
          updatedTrains.forEach(updatedTrain => {
            let find = trainData.find(t => t.train.id == updatedTrain.id);
            if (find) {
              find.train = updatedTrain;
              updatedTrainData.push(find);
            } else {
              updatedTrainData.push(new TrainData(updatedTrain, false, false));
            }
          });
          this.trains.set(updatedTrainData);
        });
      });

      this.trainSubscription.trainDrivingDirection().subscribe(data => {
        let direction = data.direction;
        if (direction) {
          this.getTrain(data.itemId).forward = direction === DRIVINGDIRECTION.Forward;
        }
      });
      this.trainSubscription.trainDrivingLevel().subscribe(data => {
        this.getTrain(data.itemId).drivingLevel = data.speed ?? 0;
      });
      this.trainSubscription.trainLightState().subscribe(data => {
        this.getTrainData(data.itemId).light = data.state!;
      });
      this.trainSubscription.trainHornState().subscribe(data => {
        this.getTrainData(data.itemId).horn = data.state!;
      });

    });
  }

  private getTrainData(id: Number | undefined): TrainData {
    return this.trains().find(e => e.train.id == id)!;
  }

  private getTrain(id: Number | undefined): Train {
    return this.getTrainData(id).train;
  }

  protected stopTrain(train: Train) {
    this.changeDrivingLevel(train, 0);
  }

  protected changeDrivingLevel(train: Train, level: number) {
    this.trainService.changeDrivingLevel(train, level).subscribe();
  }

  protected updateLight(train: Train) {
    this.trainService.toggleLight(train, !(this.getTrainData(train.id).light)).subscribe();
  }

  protected updateHorn(train: Train) {
    this.trainService.toggleHorn(train, !(this.getTrainData(train.id).horn)).subscribe();
  }

  protected drivingDirectionChanged(train: Train) {
    this.trainService.toggleDirection(train,
      train.forward ? DRIVINGDIRECTION.Forward : DRIVINGDIRECTION.Backward)
    .subscribe();
  }
}
