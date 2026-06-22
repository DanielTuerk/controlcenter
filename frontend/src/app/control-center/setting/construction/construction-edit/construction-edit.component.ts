import {Component, inject, input, OnInit, signal, ChangeDetectionStrategy} from '@angular/core';
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FloatLabelType, MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {Router, RouterLink} from "@angular/router";
import {SnackBar} from "../../../common/snack-bar.component";
import {Construction} from "../../../../../shared/openapi-gen";
import {ConstructionService} from "../../../../shared/construction.service";

@Component({
  selector: 'app-construction-edit',
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
    ReactiveFormsModule
  ],
  templateUrl: './construction-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './construction-edit.component.css'
})
export class ConstructionEditComponent implements OnInit {

  private constructionService = inject(ConstructionService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  constructionId = input.required<Number>();
  construction = signal<Construction>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
  });

  ngOnInit() {
    if (this.constructionId() && this.constructionId().toString() != "create") {
      this.constructionService.fetchConstruction(this.constructionId()).subscribe(data => {
        this.setConstruction(data);
      });
    }
  }

  onSubmit() {
    let toUpdate = this.construction();
    toUpdate.name = this.form.controls.name.getRawValue()!

    let observable;
    if (toUpdate.id === undefined) {
      observable = this.constructionService.createConstruction(toUpdate);
    } else {
      observable = this.constructionService.saveConstruction(toUpdate);
    }
    observable.subscribe(() => {
      this.snackBar.showSuccess(`construction "${toUpdate.name}" ${toUpdate.id === undefined ? 'created' : 'updated'} successfully.`);
      this.router.navigate(['/cc/settings/construction', {}]);
    })
  }

  private setConstruction(construction: Construction) {
    this.construction.set(construction);
    this.form.controls.name.setValue(construction.name!)
  }

}
