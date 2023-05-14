import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeOffRequestDialogComponent } from './time-off-request-dialog.component';

describe('TimeOffRequestDialogComponent', () => {
  let component: TimeOffRequestDialogComponent;
  let fixture: ComponentFixture<TimeOffRequestDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimeOffRequestDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimeOffRequestDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
