import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {ConstructionService} from "./shared/construction.service";
import {WebSocketService} from "./shared/websocket/websocket.service";
import {tap} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {

  private constructionService = inject(ConstructionService);
  private wsService = inject(WebSocketService);
  private router = inject(Router)

  ngOnInit() {
    this.wsService.connect();

    // fetch current construction from server side
    this.constructionService.loadCurrentConstruction().pipe(
      tap(construction => this.constructionService.updateCurrentConstruction(construction))
    ).subscribe({
      error: () => this.redirectToWelcomePage()
    });
  }

  private redirectToWelcomePage() {
    console.log("no current construction found, navigate to welcome page")
    if (!this.router.navigate(['/welcome', {}])) console.error("can't forward to welcome page");
  }
}
