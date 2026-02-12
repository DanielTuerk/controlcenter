import {EditorTrackComponent} from "./editor-track/editor-track.component";
import {EditorBlockComponent} from "./editor-block/editor-block.component";
import {EditBlockComponent} from "./editor-block/edit-block/edit-block.component";
import {Routes} from "@angular/router";

export const routes: Routes = [
  {
    path: 'track', component: EditorTrackComponent
  },
  {
    path: 'track-block',
    component: EditorBlockComponent
  },
  {
    path: 'track-block/:trackBlockId', component: EditBlockComponent
  },
  {
    path: '',
    redirectTo: 'track',
    pathMatch: 'full'
  }
];
