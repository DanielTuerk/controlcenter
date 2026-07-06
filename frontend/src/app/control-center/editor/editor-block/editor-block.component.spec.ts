import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {EditorBlockComponent} from './editor-block.component';

describe('EditorBlockComponent', () => {
  let component: EditorBlockComponent;
  let fixture: ComponentFixture<EditorBlockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditorBlockComponent],
      providers: [provideRouter([]), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditorBlockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
