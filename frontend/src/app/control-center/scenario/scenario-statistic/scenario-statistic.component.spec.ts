import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ScenarioStatisticComponent} from './scenario-statistic.component';

describe('ScenarioStatisticComponent', () => {
  let component: ScenarioStatisticComponent;
  let fixture: ComponentFixture<ScenarioStatisticComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioStatisticComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioStatisticComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
