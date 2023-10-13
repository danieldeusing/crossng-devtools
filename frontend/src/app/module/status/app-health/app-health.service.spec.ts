import { TestBed } from '@angular/core/testing';

import { AppHealthService } from './app-health.service';

describe('HealthCheckService', () => {
  let service: AppHealthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppHealthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
