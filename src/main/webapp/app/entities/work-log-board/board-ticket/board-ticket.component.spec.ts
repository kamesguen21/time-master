import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardTicketComponent } from './board-ticket.component';

describe('BoardTicketComponent', () => {
  let component: BoardTicketComponent;
  let fixture: ComponentFixture<BoardTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardTicketComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BoardTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
