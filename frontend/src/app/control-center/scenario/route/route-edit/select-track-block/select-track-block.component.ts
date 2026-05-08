import {ChangeDetectionStrategy, Component, inject, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {TrackBlock} from "../../../../../../shared/openapi-gen";
import {FormsModule} from "@angular/forms";
import {TrackBlockSelectComponent} from "../../../../common/track-block-select/track-block-select.component";

@Component({
  selector: 'app-select-track-block',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    FormsModule,
    TrackBlockSelectComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './select-track-block.component.html',
  styleUrl: './select-track-block.component.css'
})
export class SelectTrackBlockComponent {
  readonly trackBlocks = signal<TrackBlock[]>(inject<TrackBlock[]>(MAT_DIALOG_DATA));
  selectedBlock: TrackBlock | null = null;
}
