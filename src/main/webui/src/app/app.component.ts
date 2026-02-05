import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {ConstructionService} from "./shared/construction.service";
import {WebSocketService} from "./shared/websocket/websocket.service";
import {ConstructionSubscription} from "./shared/websocket/construction.subscription";
import {ConfigService, KEY_CONSTRUCTION_DEFAULT, KEY_CONSTRUCTION_SHOW_WELCOME} from "./shared/config.service";
import {EMPTY, of, switchMap, tap, throwError} from "rxjs";
import {catchError} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {

  private constructionService = inject(ConstructionService);
  private wsService = inject(WebSocketService);
  private configService = inject(ConfigService);
  private constructionSubscription= inject(ConstructionSubscription);
  private router = inject(Router)

  ngOnInit() {
    // check for current construction already set on server side and handle the event
    this.wsService.connect();
    this.constructionSubscription.currentConstruction().subscribe(event => {
      this.constructionService.updateCurrentConstruction(event.construction);
    });

    // fetch current construction from server side
    this.constructionService.loadCurrentConstruction().pipe(
      tap(construction => this.constructionService.updateCurrentConstruction(construction)),
      catchError(() => {
        console.error("no current construction");

        // if no current construction on server side, check config for default construction
        return this.configService.loadConfigValue(KEY_CONSTRUCTION_SHOW_WELCOME).pipe(
          switchMap(showWelcome => {
            if (showWelcome) {
              return of(null); // do nothing, show welcome page
            }
            return this.configService.loadConfigValue(KEY_CONSTRUCTION_DEFAULT).pipe(
              switchMap(defaultConstruction => {
                if (!defaultConstruction) {
                  return throwError(() => new Error("no default construction"));
                }
                return this.constructionService.fetchConstruction(Number(defaultConstruction)).pipe(
                  switchMap(construction =>
                    this.constructionService.selectCurrentConstruction(construction).pipe(
                      tap(() => this.constructionService.updateCurrentConstruction(construction))
                    )
                  )
                );
              })
            );
          }),
          catchError(err => {
            console.error("fallback error", err);
            this.redirectToWelcomePage();
            return EMPTY;
          })
        );
      })
    ).subscribe({
      error: () => this.redirectToWelcomePage()
    });
  }

  private redirectToWelcomePage() {
    console.log("no current construction found, navigate to welcome page")
    this.router.navigate(['/welcome', {}]);
  }
}
