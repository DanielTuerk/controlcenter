import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusSendDataDialogComponent } from './bus-send-data-dialog.component';

describe('BusSendDataDialogComponent', () => {
  let component: BusSendDataDialogComponent;
  let fixture: ComponentFixture<BusSendDataDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusSendDataDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusSendDataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
