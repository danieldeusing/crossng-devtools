import { TestBed } from '@angular/core/testing';

import { HealthStatusService } from './health-status.service';

describe('HealthCheckService', () => {
  let service: HealthStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HealthStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
