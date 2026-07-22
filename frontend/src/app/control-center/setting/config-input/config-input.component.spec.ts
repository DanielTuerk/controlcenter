import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {ConfigInputComponent} from './config-input.component';

describe('ConfigInputComponent', () => {
  let component: ConfigInputComponent;
  let fixture: ComponentFixture<ConfigInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfigInputComponent],
      providers: [provideHttpClientTesting()]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ConfigInputComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('configKey', '');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
