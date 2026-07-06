import {ChangeDetectionStrategy, Component, inject, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {TrainService} from "../../../../shared/train.service";
import {MatDivider} from "@angular/material/divider";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";

import {TrainSubscription} from "../../../../shared/websocket/train.subscription";
import {DRIVINGDIRECTION, Train, TrainFunction} from "../../../../../shared/openapi-gen";
import {RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DeviceService} from "../../../../shared/device.service";
import {TrainDirectionIcon} from "../../../common/trainDirectionIcon";

export class TrainData {
  train: Train;
  horn: boolean;
  light: boolean;
  drivingLevel: number = 0;
  forward: boolean = true;
  functionStates: Map<number, boolean> = new Map<number, boolean>();

  constructor(train: Train, horn: boolean, light: boolean, drivingLevel: number, forward: boolean) {
    this.train = train;
    this.horn = horn;
    this.light = light;
    this.drivingLevel = drivingLevel;
    this.forward = forward;
    this.functionStates = new Map<number, boolean>();
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
    RouterLink,
    FormsModule,
    TrainDirectionIcon
],
  templateUrl: './train.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './train.component.css'
})
export class TrainComponent {
  private trainService = inject(TrainService);
  private trainSubscription = inject(TrainSubscription);
  trains = signal<TrainData[]>([]);
  protected isConnected = inject(DeviceService).isConnected;

  constructor() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data.map(train => {
        return new TrainData(train, false, false, 0, true);
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
              updatedTrainData.push(new TrainData(updatedTrain, false, false, 0, true));
            }
          });
          this.trains.set(updatedTrainData);
        });
      });

      this.trainSubscription.trainDrivingDirection().subscribe(data => {
        let direction = data.direction;
        if (direction) {
          this.getTrainData(data.itemId).forward = direction === DRIVINGDIRECTION.Forward;
        }
      });
      this.trainSubscription.trainDrivingLevel().subscribe(data => {
        this.getTrainData(data.itemId).drivingLevel = data.speed ?? 0;
        this.trains.set([...this.trains()]);
      });
      this.trainSubscription.trainLightState().subscribe(data => {
        this.getTrainData(data.itemId).light = data.state!;
      });
      this.trainSubscription.trainHornState().subscribe(data => {
        this.getTrainData(data.itemId).horn = data.state!;
      });
      this.trainSubscription.trainFunctionState().subscribe(data => {
        this.getTrainData(data.itemId).functionStates.set(data.function!.id!, data.active!);
      });
    });
  }

  private getTrainData(id: Number | undefined): TrainData {
    return this.trains().find(e => e.train.id == id)!;
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

  protected drivingDirectionChanged(trainData: TrainData) {
    this.trainService.toggleDirection(trainData.train,
      trainData.forward ? DRIVINGDIRECTION.Forward : DRIVINGDIRECTION.Backward)
    .subscribe();
  }

  protected updateFunctionState(train: Train, trainFunction: TrainFunction, state: boolean) {
    this.trainService.toggleFunctionState(train, trainFunction, state).subscribe();
  }
}
