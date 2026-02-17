import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ToggleFunctionEditComponent} from './toggle-function-edit.component';

describe('TurnoutComponent', () => {
  let component: ToggleFunctionEditComponent;
  let fixture: ComponentFixture<ToggleFunctionEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToggleFunctionEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToggleFunctionEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
