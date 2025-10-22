import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

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
   * @param eventType
   */
  registerEvent<T>(eventType: string) {
    if (!this.subscriptions.has(eventType)) {
      this.subscriptions.set(eventType, new Subject());
    }
    let newVar = this.subscriptions.get(eventType)! as Subject<T>;
    return newVar.asObservable();
  }

  connect(): void {
    this.socket = new WebSocket(this.URL);

    this.socket.onopen = () => {
      console.log('WebSocket connected');
    };

    this.socket.onclose = () => {
      console.log('WebSocket disconnected');
      console.log('try reconnecting...');
      this.connect()
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
      }
    };

  }

  send(message: string): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(message);
    }
  }

}
