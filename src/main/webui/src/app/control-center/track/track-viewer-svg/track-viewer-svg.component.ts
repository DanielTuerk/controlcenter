import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
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

@Component({
  selector: 'app-track-viewer-svg',
  templateUrl: './track-viewer-svg.component.html',
  styleUrl: './track-viewer-svg.component.css',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrackViewerSvgComponent implements OnInit, AfterViewInit {
  @ViewChild('wrap', {static: true}) wrap!: ElementRef<HTMLElement>;
  @ViewChild('svgRoot', {static: true}) svg!: ElementRef<SVGSVGElement>;

  private trackComponentBuilder = inject(TrackComponentBuilder);
  private cdr = inject(ChangeDetectorRef);
  private snackBar = inject(SnackBar);

  private httpClient = inject(HttpClient);

  // TODO get from parent viewport the initial values
  svgWidth = 1000;
  svgHeight = 400;
  tileSize = AbstractTrackComponentBuilder.TILE;

  ngOnInit() {
    this.httpClient.get<[]>('/api/track')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't load track: ${err.message}`);
        return EMPTY
      })
    ).subscribe(elements => {
      elements
      .map(e => this.trackComponentBuilder.build(e))
      .forEach(e => this.addTrackPart(e));

      this.cdr.markForCheck();
    });
  }

  private addTrackPart(e: Element) {
    this.svg.nativeElement.appendChild(e);
  }

  ngAfterViewInit() {
    // get initial size
    this.updateSizeFromWrap();
  }

  @HostListener('window:resize')
  onResize() {
    this.updateSizeFromWrap();
  }

  generatePath(size: number): string {
    return `M ${size} 0 L ${size} ${size} L 0 ${size}`;
  }

  private updateSizeFromWrap() {
    const el = this.wrap?.nativeElement;
    if (el) {
      this.svgWidth = el.clientWidth;
      this.svgHeight = el.clientHeight;
      console.log("size: " + el.clientWidth + "; " + el.clientHeight);
      this.cdr.markForCheck(); // required for OnPush to let the template update
    }
  }

}

