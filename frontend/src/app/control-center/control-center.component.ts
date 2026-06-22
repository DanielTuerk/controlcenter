import {Component, ChangeDetectionStrategy} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {HeaderComponent} from "./header/header.component";
import {FooterComponent} from "./footer/footer.component";

@Component({
  selector: 'app-control-center',
  imports: [
    HeaderComponent,
    FooterComponent,
    RouterOutlet
  ],
  templateUrl: './control-center.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './control-center.component.css'
})
export class ControlCenterComponent {
}
