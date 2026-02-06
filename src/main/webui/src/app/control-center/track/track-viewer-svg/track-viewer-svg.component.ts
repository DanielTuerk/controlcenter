import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {TrackComponentBuilder} from "./track-builder/track-component-builder";
import {AbstractTrackComponentBuilder} from "./track-builder/abstract-track-component-builder";
import {TrackSubscription} from "../../../shared/websocket/track.subscription";
import {AbstractTrackPart, TrackPartStateEvent} from "../../../../shared/openapi-gen";
import {TrackElement} from "./track-element";
import {TrackService} from "../../../shared/track.service";
import {DeviceService} from "../../../shared/device.service";

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

  /**
   * Output event for a trackpart which was clicked.
   */
  @Output() trackPartClicked = new EventEmitter<TrackElement>();
  @Output() trackPartsReady = new EventEmitter<TrackElement[]>();

  private trackComponentBuilder = inject(TrackComponentBuilder);
  private cdr = inject(ChangeDetectorRef);
  private trackService = inject(TrackService);
  private trackSubscription = inject(TrackSubscription);
  private deviceService = inject(DeviceService);

  private loadedTrackParts: TrackElement[] = []

  svgWidth = 0;
  svgHeight = 0;
  tileSize = AbstractTrackComponentBuilder.TILE;
  protected isConnected = this.deviceService.isConnected;

  ngOnInit() {
    this.trackService.fetchTrackParts().subscribe(elements => {
      this.loadTrack(elements);

      this.trackSubscription.trackChanged().subscribe(event => {
        this.loadTrack(event.trackParts);
      });

      // subscribe to state changes for track parts
      this.trackSubscription.trackPartState().subscribe(event => {
        let trackElement = this.loadedTrackParts.find(
          trackElement => trackElement.trackPart.id === event.trackPartId);
        if (!trackElement) return;

        console.log("received trackPartState: ", trackElement);
        this.removeTrackPart(trackElement);
        let element = this.buildTrackPartElement(trackElement?.trackPart!, event);
        this.addTrackPart(element);
      });
    });

  }

  private loadTrack(elements: AbstractTrackPart[]) {
    this.loadedTrackParts = [];

    elements
    .map(trackPart => {
      return this.buildTrackPartElement(trackPart);
    })
    .forEach(e => this.addTrackPart(e));

    this.updateTrackDimension(elements);

    this.cdr.markForCheck();

    this.trackPartsReady.emit(this.loadedTrackParts);
  }

  private buildTrackPartElement(trackPart: AbstractTrackPart, event: TrackPartStateEvent | null = null) {
    let element = this.trackComponentBuilder.build(trackPart, event);
    let trackEvent: TrackElement = {trackPart: trackPart, svgElement: element};
    this.loadedTrackParts.push(trackEvent);

    element.addEventListener("click", () => {
      if (this.isConnected()) {
        this.trackPartClicked.emit(trackEvent);
      }
    });
    return element;
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

  private removeTrackPart(trackElement: TrackElement) {
    // remove old one if already exists
    this.loadedTrackParts = this.loadedTrackParts.filter(e => e.trackPart.id !== trackElement.trackPart.id)
    this.svg.nativeElement.removeChild(trackElement.svgElement);
  }

  generatePath(size: number): string {
    return `M ${size} 0 L ${size} ${size} L 0 ${size}`;
  }

}

