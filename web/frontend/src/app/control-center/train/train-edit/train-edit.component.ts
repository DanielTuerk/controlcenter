import {Component, inject, input, OnInit, signal} from '@angular/core';
import {TrainService} from "../../../shared/train.service";
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {Train} from "../../../../shared/openapi-gen";
import {SnackBar} from "../../common/snack-bar.component";

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
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  train = signal<Train>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    address: -1,
  });

  ngOnInit() {

    this.trainService.loadTrain(this.trainId()).subscribe(data => {
      this.setTrain(data);
    });
  }

  onSubmit() {

    let trainToUpdate = this.train();
    trainToUpdate.address = this.form.controls.address.getRawValue()!
    trainToUpdate.name = this.form.controls.name.getRawValue()!

    this.trainService.saveTrain(trainToUpdate).subscribe(data => {
      this.snackBar.showSuccess(`train "${trainToUpdate.name}" updated successfully.`);
      this.router.navigate(['/cc/train', {}]);
    })
  }

  private setTrain(train: Train) {
    this.train.set(train);
    this.form.controls.address.setValue(train.address!)
    this.form.controls.name.setValue(train.name!)
  }
}
