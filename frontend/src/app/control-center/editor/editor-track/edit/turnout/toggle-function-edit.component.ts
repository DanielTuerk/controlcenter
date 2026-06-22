import {Component, ChangeDetectionStrategy} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {EditComponent} from "../base-edit-track-part.component";
import {AbstractTrackPart, BusDataConfiguration} from "../../../../../../shared/openapi-gen";

// export class HasToggleFunction implements AbstractTrackPart {
//   id?: number;
//   toggleFunction?: BusDataConfiguration;
// }
// export type HasToggleFunction = AbstractTrackPart & {
//   toggleFunction?: BusDataConfiguration;
// };
export type HasToggleFunction = AbstractTrackPart & {
  toggleFunction?: BusDataConfiguration;
};

@Component({
  selector: 'app-toggle-function',
  imports: [
    FormsModule,
    MatFormField,
    MatInput,
    ReactiveFormsModule
  ],
  templateUrl: './toggle-function-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './toggle-function-edit.component.css'
})
export class ToggleFunctionEditComponent implements EditComponent<HasToggleFunction> {

  protected addressFormField: FormControl<number | null> = new FormControl(null);
  protected bitFormField: FormControl<number | null> = new FormControl(null);

  initTrackPart(trackPart: HasToggleFunction) {
    if (!trackPart.toggleFunction) trackPart.toggleFunction = {};
    this.addressFormField.setValue(trackPart.toggleFunction.address!)
    this.bitFormField.setValue(trackPart.toggleFunction.bit!)
  }

  formFields() {
    return {
      address: this.addressFormField,
      bit: this.bitFormField,
    }
  }

  applyChanges(trackPart: HasToggleFunction) {
    trackPart.toggleFunction!.address = this.addressFormField.getRawValue()!
    trackPart.toggleFunction!.bit = this.bitFormField.getRawValue()!
    return trackPart;
  }

}
