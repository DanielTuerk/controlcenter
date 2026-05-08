import {Component, inject} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ConstructionService} from "../../shared/construction.service";

@Component({
  selector: 'app-create-construction',
  imports: [ReactiveFormsModule],
  templateUrl: './create-construction.component.html',
  styleUrl: './create-construction.component.css'
})
export class CreateConstructionComponent {
  private constructionService = inject(ConstructionService);

  form = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  get nameIsInvalid() {
    return (this.form.touched && this.form.invalid);
  }

  onSubmit() {
    if(this.form.valid) {
      this.constructionService.createConstruction({name: this.form.controls.name.value!})
        .subscribe(data => {
          console.log(data);
          this.constructionService.selectCurrentConstruction(data)
            .subscribe();
        })
    }
  }
}
