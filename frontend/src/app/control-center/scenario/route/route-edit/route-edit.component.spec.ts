import {ComponentFixture, TestBed} from '@angular/core/testing';

import RouteEditComponent from './route-edit.component';

describe('RouteEditComponent', () => {
  let component: RouteEditComponent;
  let fixture: ComponentFixture<RouteEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RouteEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
