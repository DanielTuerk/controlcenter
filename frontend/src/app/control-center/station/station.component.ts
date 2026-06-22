import { Component, ChangeDetectionStrategy } from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-station',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle
  ],
  templateUrl: './station.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './station.component.css'
})
export class StationComponent {

}
