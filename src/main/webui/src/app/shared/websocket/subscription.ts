import {inject} from "@angular/core";
import {WebSocketService} from "./websocket.service";
import {concat, defer, Observable, of} from "rxjs";

export class Subscription {

  private wsService = inject(WebSocketService);

  /**
   *.Cache the last received event value of the event key.
   */
  private lastValueCache = new Map<string, unknown>();

  protected createEventAccessor<T>(key: string): () => Observable<T> {
    if (!this.wsService.isAlreadyRegistered(key)) {
      this.wsService.registerEventAndConsume<T>(key)
      .subscribe(event => {
        this.lastValueCache.set(key, event);
      });
    }

    const consumption = this.wsService.consumeEvent<T>(key);
    return () => defer(() => {
      if (this.lastValueCache.has(key)) {
        // first cached, then live stream
        return concat(of(this.lastValueCache.get(key) as T), consumption);
      } else {
        // no cached value: only live stream
        return consumption;
      }
    });
  }

}
