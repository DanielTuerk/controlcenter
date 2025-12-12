import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {
  MatDrawer,
  MatDrawerContainer,
  MatSidenav,
  MatSidenavContainer,
  MatSidenavContent
} from "@angular/material/sidenav";
import {MatButton} from "@angular/material/button";
import {MatListItem, MatNavList} from "@angular/material/list";

@Component({
  selector: 'app-setting',
  imports: [
    MatDrawerContainer,
    MatDrawer,
    MatButton,
    MatSidenavContainer,
    MatSidenav,
    MatSidenavContent,
    MatNavList,
    MatListItem,
    RouterLink,
    RouterOutlet,
    RouterLinkActive
  ],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent {

}
