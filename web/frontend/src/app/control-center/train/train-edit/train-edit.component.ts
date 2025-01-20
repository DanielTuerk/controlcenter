import {Component, inject, input, OnInit} from '@angular/core';
import {TrainService} from "../../../shared/train.service";
import {RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-train-edit',
  imports: [
    RouterLink,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    ReactiveFormsModule,
    MatInput,
    MatFormField,
    FormsModule,
    MatCardFooter,
    MatButton
  ],
  templateUrl: './train-edit.component.html',
  styleUrl: './train-edit.component.css'
})
export class TrainEditComponent implements OnInit {
  trainId = input.required<Number>();
  private trainService = inject(TrainService);

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    address: 0,
  });

  ngOnInit() {

    this.trainService.loadTrain(this.trainId()).subscribe(data => {
      //TODO
      // this.form.controls.address.setValue(data.address)
      // this.form.controls.name.setValue(data.name)
    });
  }

  onSubmit() {
    console.log("submit");
  }
}
