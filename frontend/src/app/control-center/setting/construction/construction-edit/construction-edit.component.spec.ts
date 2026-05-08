import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ConstructionEditComponent} from './construction-edit.component';

describe('ConstructionEditComponent', () => {
  let component: ConstructionEditComponent;
  let fixture: ComponentFixture<ConstructionEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConstructionEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConstructionEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
