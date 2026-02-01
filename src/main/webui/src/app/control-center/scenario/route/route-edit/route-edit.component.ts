import {Component, inject, input, OnInit, signal, ViewChild} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput} from "@angular/material/input";
import {FloatLabelType} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {SnackBar} from "../../../common/snack-bar.component";
import {RouteService} from "../../../../shared/route.service";
import {BlockStraight, Curve, GridPosition, Route, Straight} from "../../../../../shared/openapi-gen";
import {MatCheckbox} from "@angular/material/checkbox";
import {TrackViewerSvgComponent} from "../../../track/track-viewer-svg/track-viewer-svg.component";
import {MatChip} from "@angular/material/chips";
import {TrackElement} from "../../../track/track-viewer-svg/track-element";
import {MatDialog} from "@angular/material/dialog";
import {SelectTrackBlockComponent} from "./select-track-block/select-track-block.component";
import {NgIf} from "@angular/common";
import {SuccessIconComponent} from "../../../../shared/component/SuccessIconComponent";
import {ErrorIconComponent} from "../../../../shared/component/ErrorIconComponent";

@Component({
  selector: 'app-route-edit',
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
    MatCheckbox,
    TrackViewerSvgComponent,
    MatChip,
    NgIf,
    SuccessIconComponent,
    ErrorIconComponent
  ],
  templateUrl: './route-edit.component.html',
  styleUrl: './route-edit.component.css'
})
export class RouteEditComponent implements OnInit {

  private routeService = inject(RouteService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  routeId = input.required<Number>();
  $route = signal<Route>({});

  readonly hideRequiredControl = new FormControl(false);
  readonly floatLabelControl = new FormControl('auto' as FloatLabelType);
  readonly form = inject(FormBuilder).group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
    name: '',
    oneway: [false],
  });
  readonly selectEndBlockDialog = inject(MatDialog);

  protected COLOR_START = '#ec668c';
  protected COLOR_END = '#edb555';
  protected COLOR_WAYPOINT = 'purple';

  private selectionType: 'start' | 'end' | 'waypoint' | undefined;
  private lastStart: TrackElement | undefined;
  private lastEnd: TrackElement | undefined;
  protected trackStatus: boolean | undefined = undefined;
  protected originalBlockStraightFillColor: string | null = '';
  protected originalWaypointFillColor: string | null = '';
  protected showViewer = true;

  @ViewChild(TrackViewerSvgComponent) trackViewer!: TrackViewerSvgComponent;

  ngOnInit() {
    this.routeService.loadRoute(this.routeId()).subscribe(data => {
      this.setRoute(data);
    });
  }

  onSubmit() {
    let routeToUpdate = this.$route();
    routeToUpdate.name = this.form.controls.name.getRawValue()!
    routeToUpdate.oneway = this.form.controls.oneway.getRawValue()!

    let observable;
    let operation;
    if (routeToUpdate.id === undefined) {
      observable = this.routeService.createRoute(routeToUpdate);
      operation = 'created'
    } else {
      observable = this.routeService.saveRoute(routeToUpdate);
      operation = 'updated'
    }
    observable.subscribe(data => {
      this.snackBar.showSuccess(`route "${routeToUpdate.name}" ${operation} successfully.`);
      this.router.navigate(['/cc/scenario', {}]);
    })
  }

  private setRoute(route: Route) {
    this.$route.set(route);
    this.form.controls.name.setValue(route.name!);
    this.form.controls.oneway.setValue(route.oneway!);

    this.buildRouteOnTrack();
  }

  protected selectTrackStart() {
    this.selectionType = 'start';
  }

  protected selectTrackEnd() {
    this.selectionType = 'end';
  }

  protected selectTrackWaypoint() {
    this.selectionType = 'waypoint';
  }

  protected onTrackPartClicked(trackEvent: TrackElement) {
    // TODO cleanup code and maybe extract
    switch (this.selectionType + ':' + trackEvent.trackPart.trackPartType) {
      case 'start:BlockStraight':
        this.originalBlockStraightFillColor = trackEvent.svgElement.getAttribute('fill');
        if (this.lastStart) {
          this.lastStart.svgElement.setAttribute('fill', this.originalBlockStraightFillColor!);
        }
        trackEvent.svgElement.setAttribute('fill', this.COLOR_START);
        this.$route().start = trackEvent.trackPart;
        this.lastStart = trackEvent;

        this.buildRouteOnTrack();
        break;
      case 'end:BlockStraight':
        this.selectEndTrackBlock(trackEvent.trackPart).subscribe(result => {
          if (result) {
            this.originalBlockStraightFillColor = trackEvent.svgElement.getAttribute('fill');
            if (this.lastEnd) {
              this.lastEnd.svgElement.setAttribute('fill', this.originalBlockStraightFillColor!);
            }
            trackEvent.svgElement.setAttribute('fill', this.COLOR_END);

            this.$route().end = result;
            this.lastEnd = trackEvent;

            this.buildRouteOnTrack();
          }
        });
        break;
      case 'waypoint:Straight':
      case 'waypoint:Curve':
        if (!this.originalWaypointFillColor) {
          this.originalWaypointFillColor = trackEvent.svgElement.getAttribute('fill');
        }
        if (this.$route().waypoints &&
          this.$route().waypoints!.find(waypoint =>
            this.equalGridPos(waypoint, trackEvent.trackPart.gridPosition))) {

          this.$route().waypoints = this.$route().waypoints!.filter(waypoint => !this.equalGridPos(waypoint, trackEvent.trackPart.gridPosition))
          trackEvent.svgElement.setAttribute('fill', this.originalWaypointFillColor!);
        } else {
          let waypoint: Straight | Curve = trackEvent.trackPart;
          if (!this.$route().waypoints) {
            this.$route().waypoints = new Array<GridPosition>;
          }
          this.$route().waypoints!.push(waypoint.gridPosition!);
          trackEvent.svgElement.setAttribute('fill', this.COLOR_WAYPOINT);
        }
        this.buildRouteOnTrack();
        break;
    }
  }

  private buildRouteOnTrack() {
    this.routeService.buildTrack(this.$route()).subscribe({
      next: (track) => {
        this.trackStatus = true;
        this.$route().track = track;
        this.repaintTrackViewer();
      },
      error: () => {
        this.trackStatus = false;
        this.$route().track = {};
        this.repaintTrackViewer();
      }
    })
  }

  private repaintTrackViewer() {
    this.showViewer = false;
    setTimeout(() => this.showViewer = true);
  }

  protected selectEndTrackBlock(blockStraight: BlockStraight) {
    return this.selectEndBlockDialog.open(SelectTrackBlockComponent, {
      data: blockStraight.allTrackBlocks
    }).afterClosed();
  }

  protected onTrackReady($event: TrackElement[]) {
    let route = this.$route();
    $event.forEach(trackElement => {
      // highlight by type
      let color = undefined;
      if (trackElement.trackPart.id == route.start?.id) {
        color = this.COLOR_START;
      } else if (route.waypoints?.some(e => this.equalGridPos(e, trackElement.trackPart.gridPosition))) {
        color = this.COLOR_WAYPOINT;
      } else if (route.end
        && trackElement.trackPart.trackPartType === 'BlockStraight'
        && (trackElement.trackPart as BlockStraight).allTrackBlocks?.some(trackBlock => trackBlock.id == route.end?.id)) {
        color = this.COLOR_END;

      } else if (route.track !== undefined && route.track.gridPositions
        && route.track.gridPositions.find(g => this.equalGridPos(g, trackElement.trackPart.gridPosition))) {
        // find elements of track from route by grid pos
        color = 'blue'
      }
      if (color) {
        trackElement.svgElement.setAttribute('fill', color);
      }
    })
  }

  private equalGridPos(gridPos1: GridPosition | undefined, gridPos2: GridPosition | undefined) {
    return gridPos1 && gridPos2 && (gridPos1.x == gridPos2.x && gridPos1.y == gridPos2.y);
  }
}
