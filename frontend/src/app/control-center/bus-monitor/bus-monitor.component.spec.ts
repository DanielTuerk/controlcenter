import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {NEVER} from 'rxjs';

import {BusMonitorComponent} from './bus-monitor.component';
import {WebSocketService} from '../../shared/websocket/websocket.service';

describe('BusMonitorComponent', () => {
  let component: BusMonitorComponent;
  let fixture: ComponentFixture<BusMonitorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusMonitorComponent],
      providers: [
        provideHttpClientTesting(),
        {
          provide: WebSocketService,
          useValue: {getClientId: () => 'test-client-id', registerEventAndConsume: () => NEVER}
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
