import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { BoardTicketComponent } from './board-ticket/board-ticket.component';
import { TicketDialogComponent } from './ticket-dialog/ticket-dialog.component';
import { TimeLogDialogComponent } from './time-log-dialog/time-log-dialog.component';
import { WorkLogBoardComponent } from './board/work-log-board.component';

@NgModule({
  imports: [SharedModule, DragDropModule],
  declarations: [WorkLogBoardComponent, TimeLogDialogComponent, TicketDialogComponent, BoardTicketComponent],
  exports: [WorkLogBoardComponent],
})
export class WorkLogBoardModule {}
