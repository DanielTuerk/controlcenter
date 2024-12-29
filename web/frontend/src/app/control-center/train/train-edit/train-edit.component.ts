import {Component, inject, input, OnInit, signal} from '@angular/core';
import {TrainService} from "../../../shared/train.service";
import {Train} from "../../../../shared/gen-js/train_types";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-train-edit',
  imports: [
    RouterLink
  ],
  templateUrl: './train-edit.component.html',
  styleUrl: './train-edit.component.css'
})
export class TrainEditComponent implements OnInit {
  trainId = input.required<Number>();
  private trainService = inject(TrainService);
  // train = computed(() => this.trainService.loadTrain(this.trainId()));
  train = signal<Train | null>(null).asReadonly();

  ngOnInit() {

    // computed(() =>this.trainService.loadTrain(this.trainId()));
    this.train = this.trainService.loadTrain(this.trainId());
  }
}
