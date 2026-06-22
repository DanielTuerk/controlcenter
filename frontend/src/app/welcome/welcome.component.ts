import {Component, inject, OnInit, ChangeDetectionStrategy} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";

import {CreateConstructionComponent} from "./create-construction/create-construction.component";
import {Construction} from "../../shared/openapi-gen";

@Component({
  selector: 'app-welcome',
  imports: [
    CreateConstructionComponent
],
  templateUrl: './welcome.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent implements OnInit {

  private constructionService = inject(ConstructionService);

  constructions = this.constructionService.loadedConstructions;

  ngOnInit() {
    this.constructionService.loadConstructions()
  }

  selectCurrentConstruction(construction: Construction) {
    this.constructionService.selectCurrentConstruction(construction)
    .subscribe();
  }
}
