import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {ConstructionService} from "./shared/construction.service";
import {WebSocketService} from "./shared/websocket/websocket.service";
import {ConstructionSubscription} from "./shared/websocket/construction.subscription";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {

  private constructionService = inject(ConstructionService);
  private wsService = inject(WebSocketService);
  private constructionSubscription= inject(ConstructionSubscription);

  ngOnInit() {
    this.constructionService.loadCurrentConstruction()
    this.wsService.connect();

    this.constructionSubscription.currentConstruction().subscribe(event => {
      // TODO should be done over event or cached event
      this.constructionService.loadCurrentConstruction();
    });

  }
}
