import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {ConstructionEditComponent} from './construction-edit.component';

describe('ConstructionEditComponent', () => {
  let component: ConstructionEditComponent;
  let fixture: ComponentFixture<ConstructionEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConstructionEditComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConstructionEditComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('constructionId', 'create');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
