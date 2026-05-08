import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectTrackBlockComponent} from './select-track-block.component';

describe('SelectTrackBlockComponent', () => {
  let component: SelectTrackBlockComponent;
  let fixture: ComponentFixture<SelectTrackBlockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectTrackBlockComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectTrackBlockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
