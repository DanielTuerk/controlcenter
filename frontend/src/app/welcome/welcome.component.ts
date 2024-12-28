import {Component, inject, OnInit} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";
import {Construction} from "../shared/shared.model";
import {NgForOf} from "@angular/common";
import {CreateConstructionComponent} from "./create-construction/create-construction.component";

@Component({
  selector: 'app-welcome',
  standalone: true,
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
