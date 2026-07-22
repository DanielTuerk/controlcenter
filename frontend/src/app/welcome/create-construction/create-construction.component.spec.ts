import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateConstructionComponent } from './create-construction.component';

describe('CreateConstructionComponent', () => {
  let component: CreateConstructionComponent;
  let fixture: ComponentFixture<CreateConstructionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateConstructionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateConstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
