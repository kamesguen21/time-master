import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkLogBoardComponent } from './work-log-board.component';

describe('WorkLogBoardComponent', () => {
  let component: WorkLogBoardComponent;
  let fixture: ComponentFixture<WorkLogBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkLogBoardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkLogBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
