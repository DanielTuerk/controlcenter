import {CanActivate, Router} from "@angular/router";
import {Injectable} from "@angular/core";
import {ConstructionService} from "./shared/construction.service";

@Injectable({providedIn: 'root'})
export class CurrentConstructionGuard implements CanActivate {
  constructor(private router: Router, private constructionService: ConstructionService) {
  }

  canActivate(): boolean {
    let currentConstruction = this.constructionService.currentConstruction();
    if (!currentConstruction) {
      this.router.navigate(['/welcome']);
      return false;
    }
    return true;
  }
}
