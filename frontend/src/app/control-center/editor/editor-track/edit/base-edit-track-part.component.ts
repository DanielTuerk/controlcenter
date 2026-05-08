import {Component, Inject, Injector, OnInit, Type, ViewChild, ViewContainerRef} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {AbstractTrackPart} from "../../../../../shared/openapi-gen";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SnackBar} from "../../../common/snack-bar.component";
import {TrackService} from "../../../../shared/track.service";

export interface BaseDialogData<T extends AbstractTrackPart> {
  component: EditComponent<T>;
  componentType: Type<EditComponent<T>>;
  trackPart: T;
}

export interface EditComponent<T extends AbstractTrackPart> {
  initTrackPart(trackPart: T): void;

  applyChanges(trackPart: T): T;

  formFields(): Object;
}

@Component({
  selector: 'app-base-edit-track-part',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './base-edit-track-part.component.html',
  styleUrl: './base-edit-track-part.component.css'
})
export class BaseEditTrackPartComponent<T extends AbstractTrackPart> implements OnInit {

  @ViewChild('outlet', {read: ViewContainerRef, static: true}) vcr!: ViewContainerRef;

  private readonly childInjector?: Injector;
  private editComponentInstance?: EditComponent<T>;

  protected form!: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: BaseDialogData<T>,
              private injector: Injector,
              private snackBar: SnackBar,
              private fb: FormBuilder,
              private trackService: TrackService) {
    if (dialogData?.trackPart) {
      this.childInjector = Injector.create({
        providers: [{provide: MAT_DIALOG_DATA, useValue: dialogData}],
        parent: this.injector
      });
    } else {
      this.childInjector = this.injector;
    }
  }

  ngOnInit(): void {
    if (!this.dialogData?.componentType) return;

    // create dialog content component to have access to the reference
    let editComponentRef = this.vcr.createComponent<EditComponent<T>>(this.dialogData.componentType, {injector: this.childInjector});
    this.editComponentInstance = editComponentRef.instance;

    this.editComponentInstance.initTrackPart(this.dialogData.trackPart);

    const fields = this.editComponentInstance.formFields() as { [key: string]: any };
    this.form = this.fb.group(fields);
  }

  private getTrackPart() {
    return this.dialogData.trackPart;
  }

  protected onSubmit() {
    if (!this.editComponentInstance) {
      throw Error("no component instance")
    }
    let toUpdate: T = this.editComponentInstance.applyChanges(this.getTrackPart());
    if (toUpdate) {
      this.trackService.saveTrackPart(toUpdate).subscribe(() => {
        this.snackBar.showSuccess(`track-part "${toUpdate.id}" updated successfully.`);
      })
    }
  }

}
