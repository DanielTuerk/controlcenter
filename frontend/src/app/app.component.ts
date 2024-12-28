import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {ConstructionService} from "./shared/construction.service";

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {

  private constructionService = inject(ConstructionService);

  ngOnInit() {
    this.constructionService.loadCurrentConstruction()
  }
}
