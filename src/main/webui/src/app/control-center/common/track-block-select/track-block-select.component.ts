import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatFormField} from "@angular/material/input";
import {MatOption, MatSelect} from "@angular/material/select";
import {NgForOf} from "@angular/common";
import {TrackBlock} from "../../../../shared/openapi-gen";

@Component({
  selector: 'app-track-block-select',
  templateUrl: './track-block-select.component.html',
  standalone: true,
  imports: [
    MatFormField,
    MatSelect,
    MatOption,
    NgForOf]
})
export class TrackBlockSelectComponent {
  @Input() trackBlocks: TrackBlock[] = [];
  @Input() selected?: TrackBlock | null;
  @Input() displayFn: (item: TrackBlock) => string = (i: any) => i.displayValue;

  @Output() selectedChange = new EventEmitter<TrackBlock | null>();
  @Input() emptyLabel: string = '';

  onChange(value: TrackBlock | null) {
    this.selected = value;
    this.selectedChange.emit(value);
  }
}
