import {inject} from "@angular/core";
import {WebSocketService} from "./websocket.service";
import {Observable} from "rxjs";

export class Subscription {

  private wsService = inject(WebSocketService);

  protected createEventAccessor<T>(key: string): () => Observable<T> {
    return () => this.wsService.registerEvent<T>(key);
  }
}
