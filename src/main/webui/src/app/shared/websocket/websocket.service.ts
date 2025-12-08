import {Injectable} from '@angular/core';
import {delay, of, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private URL = 'ws://localhost:8080/websocket';
  private socket!: WebSocket;
  private subscriptions = new Map<string, Subject<any>>();

  /**
   * Register the event type (class name) to receive the messages for.
   *
   * @param eventKey
   */
  registerEventAndConsume<T>(eventKey: string) {
    console.log("RegisterEvent", eventKey);
    this.subscriptions.set(eventKey, new Subject());
    return this.consumeEvent(eventKey);
  }

  isAlreadyRegistered(eventKey: string): boolean {
    return this.subscriptions.has(eventKey);
  }

  /**
   * Receive the messages for the given event key.
   * @param eventKey
   */
  consumeEvent<T>(eventKey: string) {
    if (!this.isAlreadyRegistered(eventKey)) {
      throw new Error(`Event not registered: ${eventKey}`);
    }
    let newVar = this.subscriptions.get(eventKey)! as Subject<T>;
    return newVar.asObservable();
  }

  connect(): void {
    this.socket = new WebSocket(this.URL);

    this.socket.onopen = () => {
      console.log('WebSocket connected');
    };

    this.socket.onclose = () => {
      console.log('WebSocket disconnected');
      of(null).pipe(delay(5000)).subscribe(() => {
        console.log('try reconnecting...');
        this.connect()
      });
    };

    this.socket.onerror = (error) => {
      console.log('WebSocket connection error', error);
    }

    this.socket.onmessage = (event) => {
      const [eventName, jsonString] = event.data.split(/:(.+)/);
      const payload = JSON.parse(jsonString.trim());
      console.log('Event:', eventName,'Payload:', payload);
      if (this.subscriptions.has(eventName)) {
        this.subscriptions.get(eventName)?.next(payload);
      } else {
        console.log('no subscription for Event:', eventName);
      }
    };
  }

  send(message: string): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(message);
    }
  }

}
