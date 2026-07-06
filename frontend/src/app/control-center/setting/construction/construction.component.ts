import {ChangeDetectionStrategy, Component, inject, signal} from '@angular/core';
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
import {MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {Construction} from "../../../../shared/openapi-gen";
import {MatDialog} from "@angular/material/dialog";
import {ConstructionSubscription} from "../../../shared/websocket/construction.subscription";
import {ConstructionService} from "../../../shared/construction.service";
import {ConfirmDialogComponent} from "../../common/confirm-dialog/confirm-dialog.component";
import {MatChip} from "@angular/material/chips";
import {FormsModule} from "@angular/forms";
import {ConfigService, KEY_CONSTRUCTION_DEFAULT} from "../../../shared/config.service";
import {MatFormField, MatLabel, MatOption, MatSelect} from "@angular/material/select";

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
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatHeaderCellDef,
    MatChip,
    MatIconButton,
    MatMiniFabButton,
    FormsModule,
    MatSelect,
    MatOption,
    MatLabel,
    MatFormField
  ],
  templateUrl: './construction.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './construction.component.css'
})
export class ConstructionComponent {

  private configService = inject(ConfigService);
  private constructionService = inject(ConstructionService);
  private constructionSubscription = inject(ConstructionSubscription);

  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = ['id', 'name', 'action'];

  constructions = signal<Construction[]>([]);

  protected defaultConstruction: Construction | undefined = undefined;

  constructor() {
    this.constructionService.fetchConstructions().subscribe(data => {
      this.constructions.set(data);

      this.configService.loadConfigValue(KEY_CONSTRUCTION_DEFAULT).subscribe(event => {
        if (event === null) {
          this.defaultConstruction = undefined;
        } else {
          this.defaultConstruction = this.constructions().find(c => c.id == Number(event));
        }
      });
    })

    this.constructionSubscription.constructionDataChanged().subscribe(() => {
      this.constructionService.fetchConstructions().subscribe(data => {
        this.constructions.set(data);
      })
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
    this.constructionService.selectCurrentConstruction(construction)
    .subscribe();
  }

  protected updateDefaultConstruction() {
    if (this.defaultConstruction) {
      this.configService.saveConfigValue(KEY_CONSTRUCTION_DEFAULT, String(this.defaultConstruction.id)).subscribe();
    }
  }

}
