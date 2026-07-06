import {inject, Injectable, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {SnackBar} from "../control-center/common/snack-bar.component";
import {Construction, ConstructionDto, Scenario} from "../../shared/openapi-gen";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

@Injectable({providedIn: 'root'})
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

  fetchConstructions() {
    return this.httpClient.get<Construction[]>('/api/constructions')
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't fetch constructions: ${err.message}`);
        return EMPTY
      })
    );
  }

  loadCurrentConstruction() {
    return this.httpClient.get<Construction>('/api/current-construction')
  }

  selectCurrentConstruction(construction: Construction) {
    return this.httpClient.post<String>('/api/current-construction', '' + construction.id)
    .pipe(
      catchError((error: any) => {
        this.snackBar.showError(`can't set current constructions: ${error.message}`);
        return EMPTY;
      })
    );
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

  fetchConstruction(constructionId: Number) {
    return this.httpClient.get<Scenario>('/api/constructions/' + constructionId)
  }

  createConstruction(construction: ConstructionDto) {
    return this.httpClient.post('/api/constructions/', construction)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't create construction: ${err.message}`);
        return EMPTY
      })
    )
  }

  saveConstruction(construction: Construction) {
    return this.httpClient.put('/api/constructions/' + construction.id, construction)
    .pipe(
      catchError((err: any) => {
        this.snackBar.showError(`can't save construction ${construction.id}: ${err.message}`);
        return EMPTY
      })
    )
  }

  deleteConstruction(constructionId: Number) {
    return this.httpClient.delete('/api/constructions/' + constructionId)
    .subscribe({
      next: () => {
        this.snackBar.showSuccess(`construction ${constructionId} deleted`);
      },
      error: (error) => {
        this.snackBar.showError(`can't delete construction ${constructionId}: ${error.message}`);
      }
    });
  }
}
