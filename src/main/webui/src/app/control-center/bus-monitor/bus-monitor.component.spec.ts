import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusMonitorComponent } from './bus-monitor.component';

describe('BusMonitorComponent', () => {
  let component: BusMonitorComponent;
  let fixture: ComponentFixture<BusMonitorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusMonitorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
