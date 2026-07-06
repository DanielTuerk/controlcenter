import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {ControlCenterComponent} from './control-center.component';

describe('ControlCenterComponent', () => {
  let component: ControlCenterComponent;
  let fixture: ComponentFixture<ControlCenterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ControlCenterComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ControlCenterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
