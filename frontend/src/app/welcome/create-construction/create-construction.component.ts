import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ConstructionService} from "../../shared/construction.service";
import {SnackBar} from "../../control-center/common/snack-bar.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-construction',
  imports: [ReactiveFormsModule],
  templateUrl: './create-construction.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './create-construction.component.css'
})
export class CreateConstructionComponent {
  private constructionService = inject(ConstructionService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  form = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  get nameIsInvalid() {
    return (this.form.touched && this.form.invalid);
  }

  onSubmit() {
    if(this.form.valid) {
      let name = this.form.controls.name.value!;
      this.constructionService.createConstruction({name: name})
        .subscribe(data => {
          console.log(data);
          this.constructionService.selectCurrentConstruction(data)
            .subscribe({
              next: () => {
                this.router.navigate(['/cc']);
              },
              error: (error) => {
                this.snackBar.showError(`can't select construction ${name}: ${error.message}`);
              }
            });
        })
    }
  }
}
