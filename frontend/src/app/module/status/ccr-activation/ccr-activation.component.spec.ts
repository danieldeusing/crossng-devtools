import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CcrActivationComponent} from './ccr-activation.component';

describe('CcrActivationComponent', () => {
  let component: CcrActivationComponent;
  let fixture: ComponentFixture<CcrActivationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CcrActivationComponent]
    });
    fixture = TestBed.createComponent(CcrActivationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
