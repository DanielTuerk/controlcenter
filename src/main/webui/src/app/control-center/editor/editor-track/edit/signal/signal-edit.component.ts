import {Component, inject, OnInit, signal} from '@angular/core';
import {Signal, TrackBlock, TYPE3} from "../../../../../../shared/openapi-gen";
import {EditComponent} from "../base-edit-track-part.component";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {TrackService} from "../../../../../shared/track.service";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
  selector: 'app-signal',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatButtonToggle,
    MatButtonToggleGroup,
    FormsModule,
    MatFormField,
    MatInput,
    ReactiveFormsModule,
    MatLabel,
    NgIf,
    MatCheckbox,
    NgSwitch,
    NgSwitchCase
  ],
  templateUrl: './signal-edit.component.html',
  styleUrl: './signal-edit.component.css'
})
export class SignalEditComponent implements EditComponent<Signal>, OnInit {

  protected red1AddressFormField: FormControl<number | null> = new FormControl(null);
  protected red1BitFormField: FormControl<number | null> = new FormControl(null);
  protected red1BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected red2AddressFormField: FormControl<number | null> = new FormControl(null);
  protected red2BitFormField: FormControl<number | null> = new FormControl(null);
  protected red2BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected green1AddressFormField: FormControl<number | null> = new FormControl(null);
  protected green1BitFormField: FormControl<number | null> = new FormControl(null);
  protected green1BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected green2AddressFormField: FormControl<number | null> = new FormControl(null);
  protected green2BitFormField: FormControl<number | null> = new FormControl(null);
  protected green2BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected yellow1AddressFormField: FormControl<number | null> = new FormControl(null);
  protected yellow1BitFormField: FormControl<number | null> = new FormControl(null);
  protected yellow1BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected yellow2AddressFormField: FormControl<number | null> = new FormControl(null);
  protected yellow2BitFormField: FormControl<number | null> = new FormControl(null);
  protected yellow2BitStateFormField: FormControl<boolean | null> = new FormControl(null);

  protected whiteAddressFormField: FormControl<number | null> = new FormControl(null);
  protected whiteBitFormField: FormControl<number | null> = new FormControl(null);
  protected whiteBitStateFormField: FormControl<boolean | null> = new FormControl(null);

  private trackService = inject(TrackService);
  protected trackBlocks = signal<TrackBlock[]>([]);
  private signal: Signal | null = null;
  protected selectedSignalType: TYPE3 = TYPE3.Block;

  ngOnInit(): void {
    this.trackService.loadTrackBlocks().subscribe(data => {
      this.trackBlocks.set(data);
    });
  }

  formFields(): Object {
    return {}
  }

  initTrackPart(signal: Signal): void {
    this.signal = signal;

    this.selectedSignalType = signal.type!;

    if (!this.signal.signalConfigRed1) this.signal.signalConfigRed1 = {};
    this.red1AddressFormField.setValue(this.signal.signalConfigRed1?.address ?? null);
    this.red1BitFormField.setValue(this.signal.signalConfigRed1?.bit ?? null);
    this.red1BitStateFormField.setValue(this.signal.signalConfigRed1?.bitState ?? null);

    if (!this.signal.signalConfigRed2) this.signal.signalConfigRed2 = {};
    this.red2AddressFormField.setValue(this.signal.signalConfigRed2?.address ?? null);
    this.red2BitFormField.setValue(this.signal.signalConfigRed2?.bit ?? null);
    this.red2BitStateFormField.setValue(this.signal.signalConfigRed2?.bitState ?? null);

    if (!this.signal.signalConfigGreen1) this.signal.signalConfigGreen1 = {};
    this.green1AddressFormField.setValue(this.signal.signalConfigGreen1?.address ?? null);
    this.green1BitFormField.setValue(this.signal.signalConfigGreen1?.bit ?? null);
    this.green1BitStateFormField.setValue(this.signal.signalConfigGreen1?.bitState ?? null);

    if (!this.signal.signalConfigGreen2) this.signal.signalConfigGreen2 = {};
    this.green2AddressFormField.setValue(this.signal.signalConfigGreen2?.address ?? null);
    this.green2BitFormField.setValue(this.signal.signalConfigGreen2?.bit ?? null);
    this.green2BitStateFormField.setValue(this.signal.signalConfigGreen2?.bitState ?? null);

    if (!this.signal.signalConfigYellow1) this.signal.signalConfigYellow1 = {};
    this.yellow1AddressFormField.setValue(this.signal.signalConfigYellow1?.address ?? null);
    this.yellow1BitFormField.setValue(this.signal.signalConfigYellow1?.bit ?? null);
    this.yellow1BitStateFormField.setValue(this.signal.signalConfigYellow1?.bitState ?? null);

    if (!this.signal.signalConfigYellow2) this.signal.signalConfigYellow2 = {};
    this.yellow2AddressFormField.setValue(this.signal.signalConfigYellow2?.address ?? null);
    this.yellow2BitFormField.setValue(this.signal.signalConfigYellow2?.bit ?? null);
    this.yellow2BitStateFormField.setValue(this.signal.signalConfigYellow2?.bitState ?? null);

    if (!this.signal?.signalConfigWhite) this.signal.signalConfigWhite = {};
    this.whiteAddressFormField.setValue(this.signal.signalConfigWhite?.address ?? null);
    this.whiteBitFormField.setValue(this.signal.signalConfigWhite?.bit ?? null);
    this.whiteBitStateFormField.setValue(this.signal.signalConfigWhite?.bitState ?? null);
  }

  applyChanges(signal: Signal): Signal {
    signal.type = this.selectedSignalType;

    if (!signal.signalConfigRed1) signal.signalConfigRed1 = {};
    signal.signalConfigRed1.bus = 1;
    signal.signalConfigRed1.address = this.red1AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigRed1.bit = this.red1BitFormField.getRawValue() ?? undefined;
    signal.signalConfigRed1.bitState = this.red1BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigRed2) signal.signalConfigRed2 = {};
    signal.signalConfigRed2.bus = 1;
    signal.signalConfigRed2.address = this.red2AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigRed2.bit = this.red2BitFormField.getRawValue() ?? undefined;
    signal.signalConfigRed2.bitState = this.red2BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigGreen1) signal.signalConfigGreen1 = {};
    signal.signalConfigGreen1.bus = 1;
    signal.signalConfigGreen1.address = this.green1AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigGreen1.bit = this.green1BitFormField.getRawValue() ?? undefined;
    signal.signalConfigGreen1.bitState = this.green1BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigGreen2) signal.signalConfigGreen2 = {};
    signal.signalConfigGreen2.bus = 1;
    signal.signalConfigGreen2.address = this.green2AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigGreen2.bit = this.green2BitFormField.getRawValue() ?? undefined;
    signal.signalConfigGreen2.bitState = this.green2BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigYellow1) signal.signalConfigYellow1 = {};
    signal.signalConfigYellow1.bus = 1;
    signal.signalConfigYellow1.address = this.yellow1AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigYellow1.bit = this.yellow1BitFormField.getRawValue() ?? undefined;
    signal.signalConfigYellow1.bitState = this.yellow1BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigYellow2) signal.signalConfigYellow2 = {};
    signal.signalConfigYellow2.bus = 1;
    signal.signalConfigYellow2.address = this.yellow2AddressFormField.getRawValue() ?? undefined;
    signal.signalConfigYellow2.bit = this.yellow2BitFormField.getRawValue() ?? undefined;
    signal.signalConfigYellow2.bitState = this.yellow2BitStateFormField.getRawValue() ?? undefined;

    if (!signal.signalConfigWhite) signal.signalConfigWhite = {};
    signal.signalConfigWhite.bus = 1;
    signal.signalConfigWhite.address = this.whiteAddressFormField.getRawValue() ?? undefined;
    signal.signalConfigWhite.bit = this.whiteBitFormField.getRawValue() ?? undefined;
    signal.signalConfigWhite.bitState = this.whiteBitStateFormField.getRawValue() ?? undefined;

    return signal;
  }

  protected readonly TYPE3 = TYPE3;
}
