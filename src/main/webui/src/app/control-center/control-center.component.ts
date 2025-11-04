import {Component, inject} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";
import {RouterOutlet} from "@angular/router";
import {HeaderComponent} from "./header/header.component";
import {FooterComponent} from "./footer/footer.component";

@Component({
  selector: 'app-control-center',
  imports: [
    HeaderComponent,
    FooterComponent,
    RouterOutlet
  ],
  templateUrl: './control-center.component.html',
  styleUrl: './control-center.component.css'
})
export class ControlCenterComponent {
  private constructionService = inject(ConstructionService);
  currentConstruction = this.constructionService.currentConstruction
}
