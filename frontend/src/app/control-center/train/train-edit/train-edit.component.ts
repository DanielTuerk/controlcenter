import {ChangeDetectionStrategy, Component, effect, inject, input, signal} from '@angular/core';
import {TrainService} from "../../../shared/train.service";
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {Train, TrainDto, TrainFunction} from "../../../../shared/openapi-gen";
import {SnackBar} from "../../common/snack-bar.component";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatIcon} from "@angular/material/icon";

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
    MatButton,
    MatCell,
    MatCellDef,
    MatCheckbox,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef
  ],
  templateUrl: './train-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './train-edit.component.css'
})
export class TrainEditComponent {
  trainId = input.required<Number>();

  train = signal<Train>({});
  trainFunction = signal<TrainFunction[]>([]);

  private trainService = inject(TrainService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);

  displayedColumns: string[] = ['alias', 'address', 'bit', 'state', 'action'];

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = this.formBuilder.group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    address: -1,
    trainFunctions: this.formBuilder.array([])
  });


  constructor() {
    effect(() => {
      const trainId = this.trainId();
      if (trainId && trainId.toString() != "create") {
        this.trainService.loadTrain(trainId).subscribe(data => {
          this.setTrain(data);
        });
      }
    });
  }

  onSubmit() {
    let trainToUpdate: TrainDto = {
      name: this.form.controls.name.getRawValue()!,
      address: this.form.controls.address.getRawValue()!,
      functions: this.trainFunction()
    };

    let observable;
    let operation;
    let trainId = this.train().id;
    if (trainId === undefined) {
      observable = this.trainService.createTrain(trainToUpdate);
      operation='created'
    } else {
      observable = this.trainService.saveTrain(trainId, trainToUpdate);
      operation='updated'
    }
    observable.subscribe(() => {
      this.snackBar.showSuccess(`train "${trainToUpdate.name}" ${operation} successfully.`);
      this.router.navigate(['/cc/train', {}]);
    })
  }

  private setTrain(train: Train) {
    this.train.set(train);
    this.form.controls.address.setValue(train.address!);
    this.form.controls.name.setValue(train.name!);

    this.trainFunction.set(Array.from(train.functions ?? []));
  }

  protected deleteTrainFunction(trainFunc: TrainFunction) {
    this.trainFunction.update(items =>
      items.filter(x => x !== trainFunc)
    );
  }

  protected addTrainFunction() {
    this.trainFunction.update(items => [
      ...items,
      {alias: '', configuration: {}} as TrainFunction
    ]);
  }


  protected updateAddress(element: TrainFunction, value: string) {
    this.extracted(element).address = Number(value);
  }

  protected updateBit(element: TrainFunction, value: string) {
    this.extracted(element).bit = Number(value);
  }

  protected updateBitState(element: TrainFunction, value: string) {
    this.extracted(element).bitState = Boolean(value);
  }

  private extracted(element: TrainFunction) {
    if (!element.configuration) {
      element.configuration = {};
    }
    return element.configuration;
  }
}
