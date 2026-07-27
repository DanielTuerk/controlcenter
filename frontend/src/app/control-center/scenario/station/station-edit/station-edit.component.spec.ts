import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {StationEditComponent} from './station-edit.component';

describe('StationEditComponent', () => {
  let component: StationEditComponent;
  let fixture: ComponentFixture<StationEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StationEditComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StationEditComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('stationId', 'create');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
