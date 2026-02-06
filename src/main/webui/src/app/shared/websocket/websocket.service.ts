import {Injectable, NgZone} from '@angular/core';
import {delay, Observable, of, ReplaySubject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class WebSocketService {
  private URL = 'ws://localhost:8080/websocket';
  private socket!: WebSocket;
  private subscriptions = new Map<string, ReplaySubject<any>>();
  private clientId?: string;

  constructor(private ngZone: NgZone) {
  }

  registerEventAndConsume<T>(eventKey: string): Observable<T> {
    if (!this.subscriptions.has(eventKey)) {
      this.subscriptions.set(eventKey, this.createSubject());
    }
    return this.consumeEvent<T>(eventKey);
  }

  private createSubject() {
    // TODO buffer need to be defined
    return new ReplaySubject<any>(1000);
  }

  consumeEvent<T>(eventKey: string): Observable<T> {
    if (!this.subscriptions.has(eventKey)) {
      // optional: create subject lazily so late callers still get future events
      this.subscriptions.set(eventKey, this.createSubject());
    }
    return this.subscriptions.get(eventKey)!.asObservable() as Observable<T>;
  }

  connect(): void {
    this.socket = new WebSocket(this.URL);

    this.socket.onopen = () => console.log('WebSocket connected');

    this.socket.onclose = () => {
      console.log('WebSocket disconnected');
      of(null).pipe(delay(5000)).subscribe(() => {
        console.log('try reconnecting...');
        this.connect()
      });
    };

    this.socket.onerror = (err) => console.log('WebSocket error', err);

    this.socket.onmessage = (event) => {
      const [eventName, jsonString] = event.data.split(/:(.+)/);
      if (eventName === 'clientId') {
        this.clientId = jsonString.trim();
        return;
      }

      const payload = JSON.parse(jsonString.trim());
      console.log('Event:', eventName, 'Payload:', payload);
      // ensure we run inside Angular zone if UI updates depend on this
      this.ngZone.run(() => {
        if (!this.subscriptions.has(eventName)) {
          this.subscriptions.set(eventName, this.createSubject());
        }
        this.subscriptions.get(eventName)!.next(payload);
      });
    };
  }

  // TODO optional cleanup helpers
  destroyEvent(eventKey: string) {
    const s = this.subscriptions.get(eventKey);
    if (s) {
      s.complete();
      this.subscriptions.delete(eventKey);
    }
  }

  getClientId() {
    if (!this.clientId) throw Error('no client id available')
    return this.clientId!;
  }
}
