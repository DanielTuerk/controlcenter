import {Component, inject} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";
import {TrainComponent} from "./train/train.component";
import {RouterOutlet} from "@angular/router";
import {HeaderComponent} from "./header/header.component";

@Component({
  selector: 'app-control-center',
  standalone: true,
  imports: [
    TrainComponent,
    RouterOutlet,
    HeaderComponent
  ],
  templateUrl: './control-center.component.html',
  styleUrl: './control-center.component.css'
})
export class ControlCenterComponent {
  private constructionService = inject(ConstructionService);
  currentConstruction = this.constructionService.currentConstruction
}
