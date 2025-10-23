import {Component, inject, OnInit} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";
import {NgForOf} from "@angular/common";
import {CreateConstructionComponent} from "./create-construction/create-construction.component";
import {Construction} from "../../shared/openapi-gen";

@Component({
  selector: 'app-welcome',
  imports: [
    NgForOf,
    CreateConstructionComponent
  ],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent implements OnInit {

  private constructionService = inject(ConstructionService);

  constructions = this.constructionService.loadedConstructions;

  ngOnInit() {
    this.constructionService.loadConstructions()
  }

  selectCurrentConstruction(construction: Construction) {
    this.constructionService.selectCurrentConstruction(construction);
  }
}
