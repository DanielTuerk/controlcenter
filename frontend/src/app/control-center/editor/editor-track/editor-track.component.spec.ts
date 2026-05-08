import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EditorTrackComponent} from './editor-track.component';

describe('EditorTrackComponent', () => {
  let component: EditorTrackComponent;
  let fixture: ComponentFixture<EditorTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditorTrackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditorTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
