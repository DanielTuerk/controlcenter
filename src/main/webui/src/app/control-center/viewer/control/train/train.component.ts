import {Component, inject, OnInit, signal} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {TrainService} from "../../../../shared/train.service";
import {Train} from "../../../../../shared/openapi-gen";
import {MatDivider} from "@angular/material/divider";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {NgForOf} from "@angular/common";

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
    NgForOf
  ],
  templateUrl: './train.component.html',
  styleUrl: './train.component.css'
})
export class TrainComponent implements OnInit {
  private trainService = inject(TrainService);
  trains = signal<Train[]>([]);

  ngOnInit() {
    this.trainService.loadTrains().subscribe(data => {
      this.trains.set(data);
    })
  }
}
