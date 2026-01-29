import {Component, inject, OnInit, signal} from '@angular/core';
import {MatButton, MatIconButton, MatMiniFabButton} from "@angular/material/button";
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
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {Construction} from "../../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ConstructionSubscription} from "../../../shared/websocket/construction.subscription";
import {ConstructionService} from "../../../shared/construction.service";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {MatChip} from "@angular/material/chips";

@Component({
  selector: 'app-construction',
  imports: [
    MatButton,
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
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatHeaderCellDef,
    MatChip,
    MatIconButton,
    MatMiniFabButton
  ],
  templateUrl: './construction.component.html',
  styleUrl: './construction.component.css'
})
export class ConstructionComponent implements OnInit {

  private constructionService = inject(ConstructionService);
  private constructionSubscription = inject(ConstructionSubscription);

  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = ['id', 'name', 'action'];

  constructions = signal<Construction[]>([]);

  ngOnInit(): void {
    this.fetchConstructions();

    this.constructionSubscription.constructionDataChanged().subscribe(event => {
      this.fetchConstructions();
    });
  }

  isCurrentConstruction(construction: Construction) {
    return this.constructionService.currentConstruction() !== null
      && this.constructionService.currentConstruction()?.id === construction.id;
  }

  deleteConstruction(construction: Construction) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: construction.name
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.constructionService.deleteConstruction(construction.id!);
      }
    });
  }

  selectCurrentConstruction(construction: Construction) {
    this.constructionService.selectCurrentConstruction(construction);
  }

  private fetchConstructions() {
    this.constructionService.fetchConstructions().subscribe(data => {
      this.constructions.set(data);
    })
  }
}
