import {Component, inject, input, signal, ViewChild, ChangeDetectionStrategy} from '@angular/core';
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
import {TrackElement} from "../../../track/track-viewer-svg/track-element";
import {MatDialog} from "@angular/material/dialog";
import {SelectTrackBlockComponent} from "./select-track-block/select-track-block.component";

import {SuccessIconComponent} from "../../../../shared/component/SuccessIconComponent";
import {ErrorIconComponent} from "../../../../shared/component/ErrorIconComponent";
import {finalize} from "rxjs";
import {MatButtonToggle, MatButtonToggleChange, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatToolbar} from "@angular/material/toolbar";

export enum SELECT_TYPE {START = 'start', END = 'end', WAYPOINT = 'waypoint'}

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
    SuccessIconComponent,
    ErrorIconComponent,
    MatButtonToggle,
    MatButtonToggleGroup,
    FormsModule,
    MatToolbar
  ],
  templateUrl: './route-edit.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './route-edit.component.css'
})
export class RouteEditComponent {

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

  protected selectType: SELECT_TYPE = SELECT_TYPE.START;

  protected COLOR_START = '#ec668c';
  protected COLOR_END = '#edb555';
  protected COLOR_WAYPOINT = '#55087c';

  private lastStart: TrackElement<any> | undefined;
  private lastEnd: TrackElement<any> | undefined;
  private trackElements = signal<TrackElement<any>[]>([]);
  protected trackStatus: boolean | undefined = undefined;
  protected showViewer = true;

  @ViewChild(TrackViewerSvgComponent) trackViewer!: TrackViewerSvgComponent;

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
    observable.subscribe(() => {
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

  protected onTrackPartClicked(trackEvent: TrackElement<any>) {
    this.removeHighlight(trackEvent);

    switch (this.selectType + ':' + trackEvent.trackPart.trackPartType) {
      case SELECT_TYPE.START + ':BlockStraight':
        this.$route().start = <BlockStraight>trackEvent.trackPart;
        this.lastStart = trackEvent;

        this.buildRouteOnTrack(true);
        break;
      case SELECT_TYPE.END + ':BlockStraight':
        this.selectEndTrackBlock(trackEvent.trackPart as BlockStraight).subscribe(result => {
          if (result) {
            this.$route().end = result;
            this.lastEnd = trackEvent;

            this.buildRouteOnTrack(true);
          }
        });
        break;
      case SELECT_TYPE.WAYPOINT + ':Straight':
      case SELECT_TYPE.WAYPOINT + ':Curve':
        if (this.$route().waypoints &&
          this.$route().waypoints!.find(waypoint =>
            this.equalGridPos(waypoint, trackEvent.trackPart.gridPosition))) {
          this.$route().waypoints = this.$route().waypoints!.filter(
            waypoint => !this.equalGridPos(waypoint, trackEvent.trackPart.gridPosition));

          this.buildRouteOnTrack(true);
        } else {
          let waypoint: Straight | Curve = trackEvent.trackPart;
          if (!this.$route().waypoints) {
            this.$route().waypoints = new Array<GridPosition>;
          }
          this.$route().waypoints!.push(waypoint.gridPosition!);

          this.buildRouteOnTrack(true);
        }
        break;
    }
  }

  private buildRouteOnTrack(repaint = false) {
    this.routeService.buildTrack(this.$route())
    .pipe(
      finalize(() => {
        if (repaint) this.repaintTrackViewer();
      })
    )
    .subscribe({
      next: (track) => {
        this.trackStatus = true;
        this.$route().track = track;
      },
      error: () => {
        this.trackStatus = false;
        this.$route().track = undefined;
      }
    });
  }

  private repaintTrackViewer() {
    this.paintRoute(this.trackElements());
  }

  protected onTrackReady($event: TrackElement<any>[]) {
    this.trackElements.set($event);

    this.routeService.loadRoute(this.routeId()).subscribe(data => {

      this.setRoute(data);
      this.paintRoute($event);
    });
  }

  private paintRoute($event: TrackElement<any>[]) {
    let route = this.$route();
    $event.forEach(trackElement => {
      this.removeHighlight(trackElement);

      // highlight by type
      let color = undefined;
      if (trackElement.trackPart.id == route.start?.id
        || (this.lastStart && trackElement.trackPart.id == this.lastStart.trackPart.id)) {
        color = this.COLOR_START;
      } else if (route.waypoints?.some(e => this.equalGridPos(e, trackElement.trackPart.gridPosition))) {
        color = this.COLOR_WAYPOINT;
      } else if ((route.end
        && trackElement.trackPart.trackPartType === 'BlockStraight'
          && (trackElement.trackPart as BlockStraight).allTrackBlocks?.some(trackBlock => trackBlock.id == route.end?.id))
        || (this.lastEnd && trackElement.trackPart.id == this.lastEnd.trackPart.id)) {
        color = this.COLOR_END;

      } else if (route.track && route.track.gridPositions
        && route.track.gridPositions.find(g => this.equalGridPos(g, trackElement.trackPart.gridPosition))) {
        // find elements of track from route by grid pos
        color = 'blue'
      }
      if (color) {
        this.highlightTrackPart(trackElement.svgElement, color);
      }
    });
  }

  private removeHighlight(trackElement: TrackElement<any>) {
    trackElement.svgElement
    .querySelectorAll(".track-highlight")
    .forEach(el => el.remove());
  }

  private highlightTrackPart(svgElement: Element, color: string) {
    const bbox = (svgElement as SVGGraphicsElement).getBBox();
    const highlight = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    highlight.classList.add("track-highlight");
    highlight.setAttribute('x', String(bbox.x));
    highlight.setAttribute('y', String(bbox.y));
    highlight.setAttribute('width', String(bbox.width));
    highlight.setAttribute('height', String(bbox.height));
    highlight.setAttribute('fill', color);
    highlight.setAttribute('stroke', color);
    highlight.setAttribute('stroke-width', '4');

    svgElement.prepend(highlight);
  }

  protected selectEndTrackBlock(blockStraight: BlockStraight) {
    return this.selectEndBlockDialog.open(SelectTrackBlockComponent, {
      data: blockStraight.allTrackBlocks
    }).afterClosed();
  }

  private equalGridPos(gridPos1: GridPosition | undefined, gridPos2: GridPosition | undefined) {
    return gridPos1 && gridPos2 && (gridPos1.x == gridPos2.x && gridPos1.y == gridPos2.y);
  }

  onToggleChange(e: MatButtonToggleChange) {
    this.selectType = e.value;
  }

  protected readonly SELECT_TYPE = SELECT_TYPE;
}
