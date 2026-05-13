import {Component, inject, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {Route} from "../../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {RouteService} from "../../../shared/route.service";
import {ErrorIconComponent} from "../../../shared/component/ErrorIconComponent";
import {SuccessIconComponent} from "../../../shared/component/SuccessIconComponent";
import {ScenarioSubscription} from "../../../shared/websocket/scenario.subscription";

@Component({
  selector: 'app-route',
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatTable,
    RouterLink,
    MatHeaderCellDef,
    ErrorIconComponent,
    ErrorIconComponent,
    ErrorIconComponent,
    SuccessIconComponent
  ],
  templateUrl: './route.component.html',
  styleUrl: './route.component.css'
})
export class RouteComponent implements OnInit {

  private routeService = inject(RouteService);
  private scenarioSubscription = inject(ScenarioSubscription);

  routes = signal<Route[]>([]);
  displayedColumns: string[] = ['id', 'name', 'start', 'end', 'oneway', 'track', 'action'];
  readonly dialog = inject(MatDialog);

  ngOnInit() {
    this.scenarioSubscription.routesChanged().subscribe(() => {
      this.loadRoutes();
    });
    this.loadRoutes();
  }

  private loadRoutes() {
    this.routeService.loadRoutes().subscribe(data => {
      this.routes.set(data);
    })
  }

  deleteRoute(route: Route) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: route.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.routeService.deleteRoute(route.id!);
      }
    });
  }

}
