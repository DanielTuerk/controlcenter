import {inject} from "@angular/core";
import {WebSocketService} from "./websocket.service";
import {Observable} from "rxjs";

export class Subscription {

  private wsService = inject(WebSocketService);

  protected createEventAccessor<T>(key: string): () => Observable<T> {
    // TODO add error handling/log for error case, only return success
    return () => this.wsService.registerEvent<T>(key);
  }
}
