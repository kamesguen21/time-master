import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeLogDialogComponent } from './time-log-dialog.component';

describe('TimeLogDialogComponent', () => {
  let component: TimeLogDialogComponent;
  let fixture: ComponentFixture<TimeLogDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimeLogDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimeLogDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
