import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {ConstructionService} from "../shared/construction.service";

import {CreateConstructionComponent} from "./create-construction/create-construction.component";
import {Construction} from "../../shared/openapi-gen";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-welcome',
  imports: [
    CreateConstructionComponent
],
  templateUrl: './welcome.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent {

  private constructionService = inject(ConstructionService);
  private snackBar = inject(SnackBar);
  private router = inject(Router);

  constructions = this.constructionService.loadedConstructions;

  constructor() {
    this.constructionService.loadConstructions()
  }

  selectCurrentConstruction(construction: Construction) {
    this.constructionService.selectCurrentConstruction(construction)
      .subscribe({
        next: () => {
          this.router.navigate(['/cc']);
        },
        error: (error) => {
          this.snackBar.showError(`can't select construction ${construction.name}: ${error.message}`);
        }
      });
  }
}
