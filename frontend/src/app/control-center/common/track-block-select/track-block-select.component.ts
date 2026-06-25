import {Component, input, model, ChangeDetectionStrategy} from '@angular/core';
import {MatFormField} from "@angular/material/input";
import {MatOption, MatSelect} from "@angular/material/select";
import {TrackBlock} from "../../../../shared/openapi-gen";

@Component({
  selector: 'app-track-block-select',
  templateUrl: './track-block-select.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    MatFormField,
    MatSelect,
    MatOption
  ]
})
export class TrackBlockSelectComponent {
  trackBlocks = input<TrackBlock[]>([]);
  selected = model<TrackBlock | null | undefined>(undefined);
  displayFn = input<(item: TrackBlock) => string>((i: any) => i.displayValue);
  emptyLabel = input('');

  onChange(value: TrackBlock | null) {
    this.selected.set(value);
  }
}
