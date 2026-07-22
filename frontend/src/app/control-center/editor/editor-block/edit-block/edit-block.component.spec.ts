import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {EditBlockComponent} from './edit-block.component';

describe('EditBlockComponent', () => {
  let component: EditBlockComponent;
  let fixture: ComponentFixture<EditBlockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditBlockComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditBlockComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('trackBlockId', 0);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
