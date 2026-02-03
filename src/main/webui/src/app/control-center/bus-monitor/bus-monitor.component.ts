import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {TYPE} from "../../../shared/openapi-gen";
import {DeviceSubscription} from "../../shared/websocket/device.subscription";
import {BusService} from "../../shared/bus.service";
import {WebSocketService} from "../../shared/websocket/websocket.service";
import {BusSubscription} from "../../shared/websocket/bus.subscription";

@Component({
  selector: 'app-bus-monitor',
  imports: [
    NgForOf,
    DecimalPipe,
    NgIf
  ],
  templateUrl: './bus-monitor.component.html',
  styleUrl: './bus-monitor.component.css'
})
export class BusMonitorComponent implements OnInit, OnDestroy {

  private deviceSubscription = inject(DeviceSubscription);
  private busSubscription = inject(BusSubscription);
  private webSocketService = inject(WebSocketService);
  private busService = inject(BusService);

  protected isConnected: boolean = false;
  protected buses: Bus[] = [];

  constructor() {
    this.buses = [
      {number: 0, rows: this.createRows()},
      {number: 1, rows: this.createRows()}
    ];
  }

  ngOnInit() {
    this.deviceSubscription.deviceConnection().subscribe(device => {
      if (device.eventType === TYPE.Connected) {
        this.isConnected = true;
        this.busService.startTrackingBus(this.webSocketService.getClientId());
      }
      if (device.eventType !== TYPE.Connected) {
        this.isConnected = false;
        this.busService.stopTrackingBus(this.webSocketService.getClientId());
      }
    });
    this.busSubscription.busDataEvent().subscribe(busDataEvent => {
      if (busDataEvent.address && busDataEvent.data) {
        this.buses.filter(bus => bus.number == busDataEvent.bus)
        .flatMap(bus => bus.rows.filter(addressRow => addressRow.address == busDataEvent.address))
        .forEach(addressRow => {
          for (let i = 0; i < 8; i++) {
            addressRow.bits[i] = ((busDataEvent.data! >> i) & 1) === 1;
          }
        })
      }
    });

    // TODO register websockets for bus data changes and update UI with animation for each bit
  }

  ngOnDestroy(): void {
    this.busService.stopTrackingBus(this.webSocketService.getClientId());
  }

  private createRows(): AddressRow[] {
    return Array.from({length: 111},
      (_, i) => (
        {address: i + 1, bits: Array(8).fill(false)}
      )
    );
  }

  protected toggleBit(bus: Bus, rowIndex: number, bitIndex: number, state: boolean): void {
    this.busService.busBit({bus: bus.number, address: rowIndex, bit: bitIndex, state: state});
  }

}

export interface Bus {
  number: number;
  rows: AddressRow[];
}

export interface AddressRow {
  address: number;
  bits: boolean[];
}
