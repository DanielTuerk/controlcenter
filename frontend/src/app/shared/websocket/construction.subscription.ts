import {Injectable, Signal} from '@angular/core';
import {Construction, ConstructionDataChangedEvent, CurrentConstructionChangeEvent} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";
import {toSignal} from "@angular/core/rxjs-interop";
import {map} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ConstructionSubscription extends Subscription {

  private readonly _currentConstruction = this.createEventAccessor<CurrentConstructionChangeEvent>('CurrentConstructionChangeEvent');
  readonly currentConstruction: Signal<Construction | null> = toSignal(
    this._currentConstruction().pipe(
      map(event => event.construction)
    ),
    {initialValue: null}
  );

  readonly constructionDataChanged = this.createEventAccessor<ConstructionDataChangedEvent>('ConstructionDataChangedEvent');

}
