import { Component } from '@angular/core';
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
  styleUrl: './station.component.css'
})
export class StationComponent {

}
