import {Component, inject, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {TrainService} from "../../shared/train.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-train',
  standalone: true,
  imports: [
    NgForOf,
    RouterLink
  ],
  templateUrl: './train.component.html',
  styleUrl: './train.component.css'
})
export class TrainComponent implements OnInit {
  private trainService = inject(TrainService);

  trains = this.trainService.loadedTrains;

  ngOnInit() {
    this.trainService.loadTrains()
  }
}
