import {Component, inject, OnInit, signal} from '@angular/core';
import {EditComponent} from "../base-edit-track-part.component";
import {BlockStraight, TrackBlock} from "../../../../../../shared/openapi-gen";
import {MatFormField, MatInput} from "@angular/material/input";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {TrackBlockSelectComponent} from "../../../../common/track-block-select/track-block-select.component";
import {TrackService} from "../../../../../shared/track.service";

@Component({
  selector: 'app-track-block',
  imports: [
    MatFormField,
    MatInput,
    ReactiveFormsModule,
    TrackBlockSelectComponent
  ],
  templateUrl: './track-block-edit.component.html',
  styleUrl: './track-block-edit.component.css'
})
export class TrackBlockEditComponent implements EditComponent<BlockStraight>, OnInit {
  protected blockLengthFormField: FormControl<number | null> = new FormControl(null);
  protected selectedLeftBlock: TrackBlock | null = null;
  protected selectedMiddleBlock: TrackBlock | null = null;
  protected selectedRightBlock: TrackBlock | null = null;
  private trackService = inject(TrackService);
  protected trackBlocks = signal<TrackBlock[]>([]);
  private trackPart: BlockStraight | null = null;

  ngOnInit(): void {
    this.trackService.loadTrackBlocks().subscribe(data => {
      this.trackBlocks.set(data);
      this.updateSelectedTrackBlocks()
    });
  }

  private updateSelectedTrackBlocks() {
    if (!this.trackPart || this.trackBlocks().length === 0) return;

    const blocks = this.trackBlocks();
    this.selectedLeftBlock = blocks.find(b => b.id === this.trackPart!.leftTrackBlock?.id) ?? null;
    this.selectedMiddleBlock = blocks.find(b => b.id === this.trackPart!.middleTrackBlock?.id) ?? null;
    this.selectedRightBlock = blocks.find(b => b.id === this.trackPart!.rightTrackBlock?.id) ?? null;
  }

  formFields(): Object {
    return {
      blockLength: this.blockLengthFormField,
    }
  }

  initTrackPart(blockStraight: BlockStraight): void {
    this.blockLengthFormField.setValue(blockStraight.blockLength!);
    this.trackPart = blockStraight;
    this.updateSelectedTrackBlocks();
  }

  applyChanges(blockStraight: BlockStraight): BlockStraight {
    blockStraight.blockLength = this.blockLengthFormField.getRawValue() ?? undefined;
    blockStraight.leftTrackBlock = this.selectedLeftBlock ?? undefined;
    blockStraight.middleTrackBlock = this.selectedMiddleBlock ?? undefined;
    blockStraight.rightTrackBlock = this.selectedRightBlock ?? undefined;
    return blockStraight;
  }

}
