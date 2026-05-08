import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from "@angular/router";
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {MatCard} from "@angular/material/card";

@Component({
  selector: 'app-setting',
  imports: [
    MatSidenavContainer,
    MatSidenav,
    MatSidenavContent,
    MatNavList,
    MatListItem,
    RouterLink,
    RouterOutlet,
    MatCard
  ],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent {

}
