import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MatDialogRef} from '@angular/material/dialog';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {PlayerDialogComponent} from './player-dialog.component';

describe('PlayerDialogComponent', () => {
  let component: PlayerDialogComponent;
  let fixture: ComponentFixture<PlayerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerDialogComponent],
      providers: [
        provideHttpClientTesting(),
        {
          provide: MatDialogRef, useValue: {
            close: () => {
            }
          }
        }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PlayerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
