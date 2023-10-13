import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppHealthComponent} from './app-health.component';

describe('HealthStatusComponent', () => {
  let component: AppHealthComponent;
  let fixture: ComponentFixture<AppHealthComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AppHealthComponent]
    });
    fixture = TestBed.createComponent(AppHealthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
