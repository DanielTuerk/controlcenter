import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {TrainEditComponent} from './train-edit.component';

describe('TrainEditComponent', () => {
  let component: TrainEditComponent;
  let fixture: ComponentFixture<TrainEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainEditComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainEditComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('trainId', 'create');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
