import {Injectable, NgZone} from '@angular/core';
import {delay, Observable, of, ReplaySubject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class WebSocketService {
  // derive from the page origin so remote access (e.g. browser on desktop, backend on
  // raspberry pi) connects to the right host; in dev mode the ng serve proxy forwards
  // /websocket to the backend (see proxyconfig.json)
  private URL = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/websocket`;
  private socket!: WebSocket;
  private subscriptions = new Map<string, ReplaySubject<any>>();
  private clientId?: string;

  constructor(private ngZone: NgZone) {
  }

  registerEventAndConsume<T>(eventKey: string, replay: boolean): Observable<T> {
    if (!this.subscriptions.has(eventKey)) {
      this.subscriptions.set(eventKey, this.createSubject(replay));
    }
    return this.consumeEvent<T>(eventKey);
  }

  private createSubject(replay: boolean) {
    // TODO buffer need to be defined
    return new ReplaySubject<any>(replay ? 1000 : 1);
  }

  private consumeEvent<T>(eventKey: string): Observable<T> {
    if (!this.subscriptions.has(eventKey)) {
      throw new Error(`no event registered for: ${eventKey}`)
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
          this.subscriptions.set(eventName, this.createSubject('cacheKey' in payload));
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
