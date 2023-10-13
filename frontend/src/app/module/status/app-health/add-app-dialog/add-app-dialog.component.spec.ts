import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddAppDialogComponent} from './add-app-dialog.component';

describe('AddContainerDialogComponent', () => {
  let component: AddAppDialogComponent;
  let fixture: ComponentFixture<AddAppDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddAppDialogComponent]
    });
    fixture = TestBed.createComponent(AddAppDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
