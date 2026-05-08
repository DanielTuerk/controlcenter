import {Component} from '@angular/core';
import {RouterLink} from "@angular/router";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {MatDivider} from "@angular/material/divider";

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    MatToolbar,
    MatIcon,
    MatDivider,
    MatButton,
    MatMiniFabButton
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

}
