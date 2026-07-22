import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {merge, of, switchMap} from 'rxjs';
import {MatButton, MatIconButton} from "@angular/material/button";
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
    MatIconButton,
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
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './route.component.css'
})
export class RouteComponent {
  private routeService = inject(RouteService);
  private scenarioSubscription = inject(ScenarioSubscription);
  private dialog = inject(MatDialog);

  routes = toSignal(
    merge(of(null), this.scenarioSubscription.routeDataChanged(), this.scenarioSubscription.routesChanged()).pipe(
      switchMap(() => this.routeService.loadRoutes())
    ),
    {initialValue: []}
  );

  displayedColumns: string[] = ['id', 'name', 'start', 'end', 'oneway', 'track', 'action'];

  deleteRoute(route: Route) {
    this.dialog.open(ConfirmDialogComponent, {data: route.name})
      .afterClosed().subscribe(result => {
      if (result === true) this.routeService.deleteRoute(route.id!);
    });
  }
}
