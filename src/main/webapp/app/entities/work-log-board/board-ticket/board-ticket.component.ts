import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { TimeLogDialogComponent } from '../time-log-dialog/time-log-dialog.component';
import { ITicket } from '../../ticket/ticket.model';
import { IWorkLog } from '../../work-log/work-log.model';

@Component({
  selector: 'jhi-board-ticket',
  templateUrl: './board-ticket.component.html',
  styleUrls: ['./board-ticket.component.scss'],
})
export class BoardTicketComponent implements OnInit {
  @Input() ticket: ITicket | null | undefined;
  @Input() username: string | null | undefined;
  @Input() userId: number | null | undefined;
  @Output() refresh: EventEmitter<void> = new EventEmitter<void>();

  constructor(protected modalService: NgbModal) {}

  ngOnInit(): void {}

  getAbbreviatedName(fullName: string): string {
    const names = fullName.trim().split(' ');
    let abbreviation = '';

    if (names.length > 0) {
      abbreviation += names[0].charAt(0).toLowerCase(); // Get the first character of the first name

      if (names.length > 1) {
        abbreviation += names[names.length - 1].charAt(0).toLowerCase(); // Get the first character of the last name
      }
    }
    return abbreviation;
  }

  logTime() {
    const modalRef = this.modalService.open(TimeLogDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ticket = this.ticket;
    modalRef.componentInstance.userId = this.userId;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.refresh.emit();
    });
  }

  calculateLog(workLogsList: IWorkLog[] | null | undefined): string {
    if (!workLogsList || !workLogsList.length) {
      return '';
    }
    let durationInDays = 0;
    let remainingHours = 0;
    let timeSpent = 0;
    for (const workLogs of workLogsList) {
      if (workLogs.timeSpent) {
        timeSpent += workLogs.timeSpent;
      }
    }
    if (timeSpent) {
      const durationInHours = timeSpent;
      durationInDays += Math.floor(durationInHours / 8);
      remainingHours += durationInHours % 8;
    }
    const formattedDuration = [];
    if (durationInDays > 0) {
      formattedDuration.push(`${durationInDays}d`);
    }

    if (remainingHours > 0) {
      formattedDuration.push(`${remainingHours}h`);
    }
    return `(${formattedDuration.join(' ')})`;
  }
}
