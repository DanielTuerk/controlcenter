import {Component, ChangeDetectionStrategy, inject} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {filter} from "rxjs";
import {HeaderComponent} from "./header/header.component";
import {FooterComponent} from "./footer/footer.component";

@Component({
  selector: 'app-control-center',
  imports: [
    HeaderComponent,
    FooterComponent,
    RouterOutlet
  ],
  templateUrl: './control-center.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './control-center.component.css'
})
export class ControlCenterComponent {
  private router = inject(Router);

  constructor() {
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      takeUntilDestroyed()
    ).subscribe(() => {
      const fragment = this.router.parseUrl(this.router.url).fragment;
      if (fragment) {
        // .main scrolls internally (overflow: auto), so the router's ViewportScroller
        // (which only targets window/document) can't reach it - scroll manually instead.
        // The target page's tables populate via async HTTP calls, so .main's content
        // (and the anchor's position within it) keeps growing for a bit after
        // NavigationEnd - keep re-snapping to the anchor for ~1s while that settles,
        // instead of a single scroll attempt that lands before there's anything to scroll.
        let framesLeft = 60;
        const snap = () => {
          document.getElementById(fragment)?.scrollIntoView({behavior: 'instant', block: 'start'});
          if (framesLeft-- > 0) requestAnimationFrame(snap);
        };
        requestAnimationFrame(snap);
      }
    });
  }
}
