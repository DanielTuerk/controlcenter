import {Component, inject, input, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatFormField, MatInput} from "@angular/material/input";
import {FormBuilder, FormControl, ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {SnackBar} from "../../../common/snack-bar.component";
import {DRIVINGLEVELADJUSTTYPE, TrackBlock} from "../../../../../shared/openapi-gen";
import {FloatLabelType} from "@angular/material/form-field";
import {TrackService} from "../../../../shared/track.service";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatOption, MatSelect} from "@angular/material/select";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-edit-block',
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatCardFooter,
    MatCardHeader,
    MatCardTitle,
    MatFormField,
    MatInput,
    ReactiveFormsModule,
    RouterLink,
    MatCheckbox,
    MatSelect,
    MatOption,
    NgForOf
  ],
  templateUrl: './edit-block.component.html',
  styleUrl: './edit-block.component.css'
})
export class EditBlockComponent implements OnInit {

  trackBlockId = input.required<Number>();
  private trackService = inject(TrackService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  trackBlock = signal<TrackBlock>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = this.formBuilder.group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    address: -1,
    bit: 1,
    feedback: false,
    drivingLevelAdjustType: this.formBuilder.control<DRIVINGLEVELADJUSTTYPE>(DRIVINGLEVELADJUSTTYPE.None),
    forwardDrivingLevel: this.formBuilder.control<number | undefined>(undefined),
    backwardDrivingLevel: this.formBuilder.control<number | undefined>(undefined)
  });

  public drivingLevelOptions() {
    return [DRIVINGLEVELADJUSTTYPE.None, DRIVINGLEVELADJUSTTYPE.Enter, DRIVINGLEVELADJUSTTYPE.Exit];
  }

  ngOnInit() {
    if (Number(this.trackBlockId())) {
      this.trackService.loadTrackBlock(this.trackBlockId()).subscribe(data => {
        this.setTrackBlock(data);
      });
    }
  }

  onSubmit() {
    let toUpdate = this.trackBlock();
    toUpdate.name = this.form.controls.name.getRawValue()!
    if (!toUpdate.blockFunction) toUpdate.blockFunction = {};
    toUpdate.blockFunction!.address = this.form.controls.address.getRawValue()!
    toUpdate.blockFunction!.bit = this.form.controls.bit.getRawValue()!
    toUpdate.feedback = this.form.controls.feedback.getRawValue()!
    toUpdate.drivingLevelAdjustType = this.form.controls.drivingLevelAdjustType.getRawValue()!
    toUpdate.forwardTargetDrivingLevel = this.form.controls.forwardDrivingLevel.getRawValue()!
    toUpdate.backwardTargetDrivingLevel = this.form.controls.backwardDrivingLevel.getRawValue()!

    let observable;
    let operation;
    if (toUpdate.id === undefined) {
      observable = this.trackService.createTrackBlock(toUpdate);
      operation = 'created'
    } else {
      observable = this.trackService.saveTrackBlock(toUpdate);
      operation = 'updated'
    }
    observable.subscribe(data => {
      this.snackBar.showSuccess(`track-block "${toUpdate.name}" ${operation} successfully.`);
      this.router.navigate(['/cc/editor/track-block', {}]);
    })
  }

  private setTrackBlock(trackBlock: TrackBlock) {
    this.trackBlock.set(trackBlock);
    this.form.controls.name.setValue(trackBlock.name!)

    if (!trackBlock.blockFunction) trackBlock.blockFunction = {};
    this.form.controls.address.setValue(trackBlock.blockFunction.address!)
    this.form.controls.bit.setValue(trackBlock.blockFunction.bit!)
    this.form.controls.feedback.setValue(!!trackBlock.feedback)

    this.form.controls.drivingLevelAdjustType.setValue(
      (!trackBlock.drivingLevelAdjustType) ? DRIVINGLEVELADJUSTTYPE.None : trackBlock.drivingLevelAdjustType);
    this.form.controls.forwardDrivingLevel.setValue(trackBlock.forwardTargetDrivingLevel);
    this.form.controls.backwardDrivingLevel.setValue(trackBlock.backwardTargetDrivingLevel);
  }
}
