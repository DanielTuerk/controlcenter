import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainEditComponent } from './train-edit.component';

describe('TrainEditComponent', () => {
  let component: TrainEditComponent;
  let fixture: ComponentFixture<TrainEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
