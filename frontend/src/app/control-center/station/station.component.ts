import {ChangeDetectionStrategy, Component, computed, inject, OnDestroy, signal, WritableSignal} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatSelect, MatSelectChange} from "@angular/material/select";
import {MatOption} from "@angular/material/core";
import {Station, StationBoardArrivalEvent, StationBoardDepartureEvent} from "../../../shared/openapi-gen";
import {StationSubscription} from "../../shared/websocket/station.subscription";
import {StationService} from "../../shared/station.service";
import {MatDivider} from "@angular/material/list";

@Component({
  selector: 'app-station',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatTableModule,
    MatIconButton,
    MatIcon,
    MatDivider,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption
  ],
  templateUrl: './station.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './station.component.css'
})
export class StationComponent implements OnDestroy {

  private stationSubscription = inject(StationSubscription);
  private stationService = inject(StationService);

  protected arrivals = signal<StationBoardArrivalEvent[]>([]);
  protected departures = signal<StationBoardDepartureEvent[]>([]);

  protected stations = signal<Station[]>([]);
  /* '' is used as the "all stations" sentinel instead of null/undefined, because
   * mat-select treats null/undefined as "nothing selected" and won't render an
   * option bound to those values as the chosen one.
   */
  protected readonly ALL_STATIONS = '';
  protected selectedStationId = signal<number | string>(this.ALL_STATIONS);

  protected filteredArrivals = computed(() =>
    this.sortByTime(this.filterByStation(this.arrivals(), this.selectedStationId())));
  protected filteredDepartures = computed(() =>
    this.sortByTime(this.filterByStation(this.departures(), this.selectedStationId())));

  protected arrivalColumns: string[] = ['station', 'platform', 'train', 'time', 'source', 'information'];
  protected departureColumns: string[] = ['station', 'platform', 'train', 'time', 'target', 'information'];

  private arrivalTtlTimers = new Map<string, ReturnType<typeof setTimeout>>();
  private departureTtlTimers = new Map<string, ReturnType<typeof setTimeout>>();

  constructor() {
    this.stationService.loadStations().subscribe(stations => this.stations.set(stations));

    this.stationSubscription.stationBoardArrival().subscribe(event =>
      this.upsertWithTtl(this.arrivals, this.arrivalTtlTimers, event));

    this.stationSubscription.stationBoardDeparture().subscribe(event =>
      this.upsertWithTtl(this.departures, this.departureTtlTimers, event));
  }

  protected onStationSelectionChange(change: MatSelectChange): void {
    this.selectedStationId.set(change.value);
  }

  ngOnDestroy(): void {
    [this.arrivalTtlTimers, this.departureTtlTimers]
      .forEach(timers => timers.forEach(timer => clearTimeout(timer)));
  }

  protected openPopout(): void {
    window.open('/cc/station-board-popout', 'stationBoardPopout', 'popup,width=1200,height=800');
  }

  private upsertWithTtl<T extends { stationInfo?: { key?: string }, ttlInMs?: number }>(
    list: WritableSignal<T[]>, timers: Map<string, ReturnType<typeof setTimeout>>, event: T
  ): void {
    list.update(entries => this.upsertByKey(entries, event));

    const key = event.stationInfo?.key;
    if (key === undefined) return;

    clearTimeout(timers.get(key));
    timers.set(key, setTimeout(() => {
      list.update(entries => entries.filter(entry => entry.stationInfo?.key !== key));
      timers.delete(key);
    }, event.ttlInMs ?? 0));
  }

  private upsertByKey<T extends { stationInfo?: { key?: string } }>(list: T[], event: T): T[] {
    const index = list.findIndex(entry => entry.stationInfo?.key === event.stationInfo?.key);
    if (index === -1) {
      return [...list, event];
    }
    return list.map((entry, i) => i === index ? event : entry);
  }

  private sortByTime<T extends { stationInfo?: { timeText?: string } }>(list: T[]): T[] {
    return [...list].sort((a, b) =>
      (a.stationInfo?.timeText ?? '').localeCompare(b.stationInfo?.timeText ?? ''));
  }

  private filterByStation<T extends {
    stationInfo?: { stationId?: number }
  }>(list: T[], stationId: number | string): T[] {
    if (stationId === this.ALL_STATIONS) {
      return list;
    }
    return list.filter(entry => entry.stationInfo?.stationId === stationId);
  }
}
