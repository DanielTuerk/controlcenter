import {inject, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Construction} from "./shared.model";
import {Router} from "@angular/router";

export class ConstructionService {

  private httpClient = inject(HttpClient);
  private router= inject(Router)
  private constructions = signal<Construction[]>([]);
  private _currentConstruction = signal<Construction | null>(null);

  loadedConstructions = this.constructions.asReadonly();
  currentConstruction = this._currentConstruction.asReadonly();

  loadConstructions() {
    this.httpClient.get<Construction[]>('/api/construction')
    .subscribe({
      next: (constructions) => {
        console.log(constructions);
        this.constructions.set(constructions);
      },
      error: (error) => {
        console.error(error)
      },
      complete: () => {
        console.log('done')
      }
    })
  }

  selectCurrentConstruction(construction: Construction) {
    this.httpClient.post<String>('/api/construction/current', '' + construction.id)
    .subscribe(
      {
        error: (error) => {
          console.error(error)
        },
        complete: () => {
          console.log('done selectCurrentConstruction')

          this.loadCurrentConstruction();
        }
      });
    // .pipe();
  }

  loadCurrentConstruction() {
    this.httpClient.get<Construction>('/api/construction/current')
    .subscribe(
      {
        next: (construction) => {
          console.log('current construction: ' + construction);
          this._currentConstruction.set(construction);
          if (construction===null) {

            this.router.navigate(['/welcome', { }]);

          }else {
            this.router.navigate(['/cc', { }]);
          }
        },
        error: (error) => {
          console.error(error)
        },
        complete: () => {
          console.log('done loadCurrentConstruction')
        }
      });
  }
}
