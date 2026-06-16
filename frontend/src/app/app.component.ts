import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {ConstructionService} from "./shared/construction.service";
import {WebSocketService} from "./shared/websocket/websocket.service";
import {ConstructionSubscription} from "./shared/websocket/construction.subscription";
import {tap} from "rxjs";
import {DeviceService} from "./shared/device.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {

  private deviceService = inject(DeviceService);
  private constructionService = inject(ConstructionService);
  private wsService = inject(WebSocketService);
  private constructionSubscription= inject(ConstructionSubscription);
  private router = inject(Router)

  ngOnInit() {
    // TODO find better way
    this.deviceService.init();

    // check for current construction already set on server side and handle the event
    this.wsService.connect();
    this.constructionSubscription.currentConstruction().subscribe(event => {
      this.constructionService.updateCurrentConstruction(event.construction);
    });

    // fetch current construction from server side
    this.constructionService.loadCurrentConstruction().pipe(
      tap(construction => this.constructionService.updateCurrentConstruction(construction))
    ).subscribe({
      error: () => this.redirectToWelcomePage()
    });
  }

  private redirectToWelcomePage() {
    console.log("no current construction found, navigate to welcome page")
    this.router.navigate(['/welcome', {}]);
  }
}
