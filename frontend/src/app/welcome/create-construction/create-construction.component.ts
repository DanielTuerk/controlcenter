import {Component} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";

@Component({
  selector: 'app-create-construction',
  imports: [ReactiveFormsModule],
  templateUrl: './create-construction.component.html',
  styleUrl: './create-construction.component.css'
})
export class CreateConstructionComponent {
  form = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  get nameIsInvalid() {
    return (this.form.touched && this.form.invalid);
  }

  onSubmit() {
    // TODO
    if(this.form.valid) {
      console.log('ok');
    }
  }
}
