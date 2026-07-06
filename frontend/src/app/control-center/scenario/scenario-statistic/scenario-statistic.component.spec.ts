import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideHttpClientTesting} from '@angular/common/http/testing';

import {ScenarioStatisticComponent} from './scenario-statistic.component';

describe('ScenarioStatisticComponent', () => {
  let component: ScenarioStatisticComponent;
  let fixture: ComponentFixture<ScenarioStatisticComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioStatisticComponent],
      providers: [provideHttpClientTesting()]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioStatisticComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('scenarioId', 1);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
