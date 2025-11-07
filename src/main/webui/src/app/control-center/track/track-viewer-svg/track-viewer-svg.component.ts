import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  OnInit,
  ViewChild
} from '@angular/core';
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {SnackBar} from "../../common/snack-bar.component";
import {HttpClient} from "@angular/common/http";
import {TrackComponentBuilder} from "./track-builder/track-component-builder";
import {AbstractTrackComponentBuilder} from "./track-builder/abstract-track-component-builder";
import {TrackSubscription} from "../../../shared/websocket/track.subscription";
import {AbstractTrackPart} from "../../../../shared/openapi-gen";

@Component({
  selector: 'app-track-viewer-svg',
  templateUrl: './track-viewer-svg.component.html',
  styleUrl: './track-viewer-svg.component.css',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrackViewerSvgComponent implements OnInit {
  @ViewChild('svgRoot', {static: true}) svg!: ElementRef<SVGSVGElement>;

  private trackComponentBuilder = inject(TrackComponentBuilder);
  private cdr = inject(ChangeDetectorRef);
  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);
  private trackSubscription = inject(TrackSubscription);

  svgWidth = 0;
  svgHeight = 0;
  tileSize = AbstractTrackComponentBuilder.TILE;

  ngOnInit() {
    this.httpClient.get<[]>('/api/track')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load track: ${err.message}`);
        return EMPTY
      })
    ).subscribe(elements => {
      this.loadTrack(elements);
    });

    this.trackSubscription.trackChanged().subscribe(event => {
      this.loadTrack(event.trackParts);
    });
  }

  private loadTrack(elements: AbstractTrackPart[]) {
    elements
    .map(e => this.trackComponentBuilder.build(e))
    .forEach(e => {
      this.addTrackPart(e)
    });

    this.updateTrackDimension(elements);

    this.cdr.markForCheck();
  }

  private updateTrackDimension(elements: AbstractTrackPart[]) {
    const {maxX, maxY} = elements.reduce(
      (acc, el) => {
        const pos = el.gridPosition;
        if (pos?.x != null && pos?.y != null) {
          acc.maxX = Math.max(acc.maxX, pos.x);
          acc.maxY = Math.max(acc.maxY, pos.y);
        }
        return acc;
      },
      {maxX: 0, maxY: 0}
    );

    this.svgWidth = (maxX + 2) * this.tileSize;
    this.svgHeight = (maxY + 2) * this.tileSize;
  }

  private addTrackPart(e: Element) {
    this.svg.nativeElement.appendChild(e);
  }

  generatePath(size: number): string {
    return `M ${size} 0 L ${size} ${size} L 0 ${size}`;
  }

}

