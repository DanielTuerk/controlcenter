import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  computed,
  ElementRef,
  inject,
  input,
  output,
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
export class TrackViewerSvgComponent {

  @ViewChild('container', {static: true}) private _container!: ElementRef<HTMLDivElement>;
  @ViewChild('svgContentContainer', {static: true}) private _svgContentContainer!: ElementRef<SVGElement>;

  get container(): ElementRef<HTMLDivElement> {
    return this._container;
  }

  overrideIsConnected = input(false);
  trackPartClicked = output<TrackElement<any>>();
  trackPartsReady = output<TrackElement<any>[]>();

  private trackComponentBuilder = inject(TrackComponentBuilder);
  private cdr = inject(ChangeDetectorRef);
  private trackService = inject(TrackService);
  private trackSubscription = inject(TrackSubscription);
  private deviceService = inject(DeviceService);

  private loadedTrackParts: TrackElement<TrackPartStateEvent>[] = [];

  svgWidth = 0;
  svgHeight = 0;
  tileSize = AbstractTrackComponentBuilder.TILE;

  protected isConnected = computed(() =>
    this.overrideIsConnected() ? true : this.deviceService.isConnected());

  constructor() {
    this.trackService.fetchTrackParts().subscribe(elements => {
      this.loadTrack(elements);

      this.trackSubscription.trackChanged().subscribe(() => {
        console.log("trackChanged");
        this.reloadTrack();
      });

      this.trackSubscription.trackPartDataChangedEvent().subscribe(() => {
        console.log("trackPartDataChangedEvent");
        this.reloadTrack();
      });
    });
  }

  public reloadTrack() {
    console.log("reload track");
    this.trackService.fetchTrackParts().subscribe(elements => {
      const container = this._svgContentContainer.nativeElement;
      while (container.firstChild) {
        container.removeChild(container.firstChild);
      }
      this.loadTrack(elements);
    });
  }

  private loadTrack(elements: AbstractTrackPart[]) {
    console.log("load track");
    this.loadedTrackParts = [];

    elements
      .map(trackPart => this.buildTrackPartElement(trackPart))
      .forEach(e => this.addTrackPart(e));

    this.updateTrackDimension(elements);
    this.cdr.markForCheck();
    this.trackPartsReady.emit(this.loadedTrackParts);

    this.trackSubscription.trackPartState().subscribe(event => {
      this.consumeTrackEvent('trackPartState', event, event.trackPartId!);
    });
    this.trackSubscription.signalFunctionState().subscribe(event => {
      this.consumeTrackEvent('signalFunctionState', event, event.signalId!);
    });
    this.trackSubscription.trackPartBlock().subscribe(event => {
      this.consumeTrackEvent('trackPartBlock', event, event.trackPartId!);
    });

    console.log("load finished");
  }

  private buildTrackPartElement<E extends TrackPartStateEvent>(trackPart: AbstractTrackPart, event: E | null = null) {
    let element = this.trackComponentBuilder.build(trackPart, event);
    let trackEvent: TrackElement<TrackPartStateEvent> = {trackPart: trackPart, svgElement: element, lastEvent: event};
    this.loadedTrackParts.push(trackEvent);

    element.addEventListener("click", () => {
      if (this.isConnected()) {
        this.trackPartClicked.emit(trackEvent);
      }
    });
    return element;
  }

  private consumeTrackEvent(name: string, event: any, trackPartId: number) {
    let trackElement = this.loadedTrackParts.find(
      trackElement => trackElement.trackPart.id === trackPartId);
    if (!trackElement) return;

    console.log(`received ${name}: `, trackElement);
    this.removeTrackPart(trackElement);
    let element = this.buildTrackPartElement(trackElement?.trackPart!, event);
    this.addTrackPart(element);
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
    this._svgContentContainer.nativeElement.appendChild(e);
  }

  private removeTrackPart(trackElement: TrackElement<TrackPartStateEvent>) {
    this.loadedTrackParts = this.loadedTrackParts.filter(e => e.trackPart.id !== trackElement.trackPart.id);
    this._svgContentContainer.nativeElement.removeChild(trackElement.svgElement);
  }

  protected generatePath(size: number): string {
    return `M ${size} 0 L ${size} ${size} L 0 ${size}`;
  }

  repaintTrackPart($event: TrackElement<any>) {
    this.removeTrackPart($event);
    this.addTrackPart(this.buildTrackPartElement($event.trackPart));
  }

  addNewTrackPart($event: TrackElement<any>) {
    let element = this.buildTrackPartElement($event.trackPart);
    this.addTrackPart(element);
    return element;
  }
}
