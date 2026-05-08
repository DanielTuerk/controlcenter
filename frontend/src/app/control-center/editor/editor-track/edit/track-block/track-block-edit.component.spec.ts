import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TrackBlockEditComponent} from './track-block-edit.component';

describe('TrackBlockComponent', () => {
  let component: TrackBlockEditComponent;
  let fixture: ComponentFixture<TrackBlockEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackBlockEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackBlockEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
