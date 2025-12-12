import {Component, inject, input, OnInit, signal} from '@angular/core';
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FloatLabelType, MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {Router, RouterLink} from "@angular/router";
import {DeviceService} from "../../../shared/device.service";
import {SnackBar} from "../../common/snack-bar.component";
import {DeviceInfo, DEVICETYPE} from "../../../../shared/openapi-gen";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";

@Component({
  selector: 'app-device-edit',
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardFooter,
    MatCardHeader,
    MatCardTitle,
    MatFormField,
    MatInput,
    RouterLink,
    ReactiveFormsModule,
    MatOption,
    MatSelect
  ],
  templateUrl: './device-edit.component.html',
  styleUrl: './device-edit.component.css'
})
export class DeviceEditComponent implements OnInit {

  protected readonly DEVICE_TYPES = [DEVICETYPE.Serial, DEVICETYPE.Test];

  private deviceService = inject(DeviceService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  deviceId = input.required<Number>();
  device = signal<DeviceInfo>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    key: '',
    type: '',
  });

  ngOnInit() {
    this.deviceService.loadDevice(this.deviceId()).subscribe(data => {
      this.setDevice(data);
    });
  }

  onSubmit() {
    let deviceToUpdate = this.device();
    deviceToUpdate.key = this.form.controls.key.getRawValue()!

    const type = this.form.controls.type.getRawValue() as string;
    if (!(Object.values(DEVICETYPE) as DEVICETYPE[]).includes(type as DEVICETYPE)) {
      throw new Error("Unknown device type");
    }
    deviceToUpdate.type = type as DEVICETYPE;

    let observable;
    if (deviceToUpdate.id === undefined) {
      observable = this.deviceService.createDevice(deviceToUpdate);
    } else {
      observable = this.deviceService.saveDevice(deviceToUpdate);
    }
    observable.subscribe(data => {
      this.snackBar.showSuccess(`device "${deviceToUpdate.key}" ${deviceToUpdate.id === undefined ? 'created' : 'updated'} successfully.`);
      this.router.navigate(['/cc/settings/device', {}]);
    })
  }

  private setDevice(device: DeviceInfo) {
    this.device.set(device);
    this.form.controls.key.setValue(device.key!)
    this.form.controls.type.setValue(device.type!.valueOf())
  }

}
