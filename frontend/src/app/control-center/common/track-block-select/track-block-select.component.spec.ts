import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TrackBlockSelectComponent} from './track-block-select.component';

describe('TrackBlockSelectComponent', () => {
  let component: TrackBlockSelectComponent;
  let fixture: ComponentFixture<TrackBlockSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackBlockSelectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackBlockSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
