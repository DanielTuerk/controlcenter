import {Component, inject, OnInit} from '@angular/core';
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {filter} from "rxjs";

@Component({
  selector: 'app-editor',
  imports: [
    MatTab,
    MatTabGroup,
    RouterOutlet
  ],
  templateUrl: './editor.component.html',
  styleUrl: './editor.component.css'
})
export class EditorComponent implements OnInit {

  private router = inject(Router);
  selectedIndex = 0;

  ngOnInit() {
    this.selectedIndex = this.indexFromUrl(this.router.url);
    this.router.events.pipe(filter(e => e instanceof NavigationEnd))
    .subscribe((e: NavigationEnd) => {
      this.selectedIndex = this.indexFromUrl(e.urlAfterRedirects);
    });
  }

  onTabChange(index: number) {
    const url = '/cc/editor' + (index === 1 ? '/track-block' : '/track');
    this.router.navigateByUrl(url);
  }

  private indexFromUrl(url: string): number {
    return url.includes('/cc/editor/track-block') ? 1 : 0;
  }

}
