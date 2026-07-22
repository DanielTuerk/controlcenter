import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

import {BusSendDataDialogComponent} from './bus-send-data-dialog.component';

describe('BusSendDataDialogComponent', () => {
  let component: BusSendDataDialogComponent;
  let fixture: ComponentFixture<BusSendDataDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusSendDataDialogComponent],
      providers: [
        {
          provide: MatDialogRef, useValue: {
            close: () => {
            }
          }
        },
        {provide: MAT_DIALOG_DATA, useValue: {bus: 0, address: 1, value: 0}}
      ]
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
