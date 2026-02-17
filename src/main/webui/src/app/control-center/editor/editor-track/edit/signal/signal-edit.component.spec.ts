import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SignalEditComponent} from './signal-edit.component';

describe('SignalComponent', () => {
  let component: SignalEditComponent;
  let fixture: ComponentFixture<SignalEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignalEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignalEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
