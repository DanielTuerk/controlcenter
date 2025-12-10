import {inject, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {Construction} from "../../shared/openapi-gen";

export class ConstructionService {

  private httpClient = inject(HttpClient);
  private router= inject(Router)
  private snackBar = inject(SnackBar);
  private constructions = signal<Construction[]>([]);
  private _currentConstruction = signal<Construction | null>(null);

  loadedConstructions = this.constructions.asReadonly();
  currentConstruction = this._currentConstruction.asReadonly();

  loadConstructions() {
    this.httpClient.get<Construction[]>('/api/constructions')
    .subscribe({
      next: (constructions) => {
        this.constructions.set(constructions);
      },
      error: (error) => {
        this.snackBar.showError(`can't load constructions: ${error.message}`);
      }
    })
  }

  loadCurrentConstruction() {
    this.httpClient.get<Construction>('/api/current-construction')
    .subscribe(
      {
        next: (construction) => {
          this.updateCurrentConstruction(construction);
        },
        error: (error) => {
          console.log("no current construction found, navigate to welcome page", error)
          this.router.navigate(['/welcome', {}]);
        }
      });
  }

  selectCurrentConstruction(construction: Construction) {
    this.httpClient.post<String>('/api/current-construction', '' + construction.id)
    .subscribe({
        error: (error) => {
          this.snackBar.showError(`can't set current constructions: ${error.message}`);
        }
      });
  }

  updateCurrentConstruction(construction: Construction) {
    if(this.currentConstruction() !== null && this.currentConstruction()?.id === construction.id) {
      // ignore already set construction (maybe cached event after reconnect)
      return;
    }
    console.log(`current construction: ${construction.name} (${construction.id})`);
    this._currentConstruction.set(construction);
    let ccPath = '/cc';
    if (!this.router.url.startsWith(ccPath)) {
      this.router.navigate([ccPath, {}]);
    }
  }
}
