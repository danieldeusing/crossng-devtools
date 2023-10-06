import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddContainerDialogComponent } from './add-container-dialog.component';

describe('AddContainerDialogComponent', () => {
  let component: AddContainerDialogComponent;
  let fixture: ComponentFixture<AddContainerDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddContainerDialogComponent]
    });
    fixture = TestBed.createComponent(AddContainerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
