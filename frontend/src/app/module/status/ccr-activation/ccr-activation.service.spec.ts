import {TestBed} from '@angular/core/testing';

import {CcrActivationService} from './ccr-activation.service';

describe('CcrActivationService', () => {
  let service: CcrActivationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CcrActivationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
