import {Injectable} from '@angular/core';
import {CurrentConstructionChangeEvent} from "../../../shared/openapi-gen";
import {Subscription} from "./subscription";

@Injectable({
  providedIn: 'root'
})
export class ConstructionSubscription extends Subscription {

  readonly currentConstruction = this.createEventAccessor<CurrentConstructionChangeEvent>('CurrentConstructionChangeEvent');

}
