import {inject} from '@angular/core';
import {Observable} from 'rxjs';
import {WebSocketService} from './websocket.service';

export class Subscription {
  private wsService = inject(WebSocketService);

  protected createEventAccessor<T>(key: string, replay = false): () => Observable<T> {
    return () => this.wsService.registerEventAndConsume<T>(key, replay);
  }
}
