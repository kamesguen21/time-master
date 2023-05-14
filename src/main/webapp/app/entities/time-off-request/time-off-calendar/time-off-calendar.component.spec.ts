import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeOffCalendarComponent } from './time-off-calendar.component';

describe('TimeOffCalendarComponent', () => {
  let component: TimeOffCalendarComponent;
  let fixture: ComponentFixture<TimeOffCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimeOffCalendarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimeOffCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
