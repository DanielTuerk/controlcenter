import {ChangeDetectionStrategy, Component, effect, inject, input, signal} from '@angular/core';
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FloatLabelType, MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatSelect} from "@angular/material/select";
import {MatOption} from "@angular/material/core";
import {MatList, MatListItem} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {Router, RouterLink} from "@angular/router";
import {SnackBar} from "../../../common/snack-bar.component";
import {BlockStraight, Station, StationPlatform} from "../../../../../shared/openapi-gen";
import {StationService} from "../../../../shared/station.service";
import {TrackService} from "../../../../shared/track.service";

@Component({
  selector: 'app-station-edit',
  imports: [
    FormsModule,
    MatButton,
    MatMiniFabButton,
    MatCard,
    MatCardContent,
    MatCardFooter,
    MatCardHeader,
    MatCardTitle,
    MatFormField,
    MatInput,
    MatSelect,
    MatOption,
    MatList,
    MatListItem,
    MatIcon,
    RouterLink,
    ReactiveFormsModule
  ],
  templateUrl: './station-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './station-edit.component.css'
})
export class StationEditComponent {

  private stationService = inject(StationService);
  private trackService = inject(TrackService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  stationId = input.required<Number>();
  station = signal<Station>({});
  blockStraights = signal<BlockStraight[]>([]);

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
  });

  constructor() {
    effect(() => {
      const stationId = this.stationId();
      if (stationId && stationId.toString() != "create") {
        this.stationService.loadStation(stationId).subscribe(data => {
          this.setStation(data);
        });
      }
    });
    this.loadBlockStraights();
  }

  private loadBlockStraights() {
    this.trackService.fetchTrackParts().subscribe(data => {
      this.blockStraights.set((data as any[]).filter(part => part.trackPartType === 'BlockStraight') as BlockStraight[]);
    });
  }

  protected addPlatform() {
    this.station.update(station => {
      const platforms = station.platforms ?? [];
      return {
        ...station,
        platforms: [...platforms, {id: undefined, name: '', blockStraights: []}]
      };
    });
  }

  protected removePlatform(platform: StationPlatform) {
    this.station.update(station => {
      const platforms = station.platforms ?? [];
      return {...station, platforms: platforms.filter(p => p !== platform)};
    });
  }

  protected addBlockStraight(platform: StationPlatform, blockStraight: BlockStraight | undefined) {
    if (!blockStraight) {
      return;
    }
    this.updatePlatform(platform, p => {
      const blockStraights = p.blockStraights ?? [];
      if (blockStraights.some(bs => bs.id === blockStraight.id)) {
        return p;
      }
      return {...p, blockStraights: [...blockStraights, blockStraight]};
    });
  }

  protected removeBlockStraight(platform: StationPlatform, blockStraight: BlockStraight) {
    this.updatePlatform(platform, p => ({
      ...p,
      blockStraights: (p.blockStraights ?? []).filter(bs => bs !== blockStraight)
    }));
  }

  protected updatePlatformName(platform: StationPlatform, name: string) {
    this.updatePlatform(platform, p => ({...p, name}));
  }

  private updatePlatform(platform: StationPlatform, updateFn: (platform: StationPlatform) => StationPlatform) {
    this.station.update(station => {
      const platforms = station.platforms ?? [];
      const index = platforms.indexOf(platform);
      if (index < 0) {
        return station;
      }
      const updatedPlatforms = [...platforms];
      updatedPlatforms[index] = updateFn(platform);
      return {...station, platforms: updatedPlatforms};
    });
  }

  onSubmit() {
    let toUpdate = this.station();
    toUpdate.name = this.form.controls.name.getRawValue()!

    let observable;
    if (toUpdate.id === undefined) {
      observable = this.stationService.createStation(toUpdate);
    } else {
      observable = this.stationService.saveStation(toUpdate);
    }
    observable.subscribe(() => {
      this.snackBar.showSuccess(`station "${toUpdate.name}" ${toUpdate.id === undefined ? 'created' : 'updated'} successfully.`);
      this.router.navigate(['/cc/scenario', {}]);
    })
  }

  private setStation(station: Station) {
    this.station.set(station);
    this.form.controls.name.setValue(station.name!)
  }

}
