import { Component, OnInit } from '@angular/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/daygrid';
import { CalendarOptions } from '@fullcalendar/core';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { AccountService } from '../../../core/auth/account.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TimeOffRequestDialogComponent } from '../time-off-request-dialog/time-off-request-dialog.component';

@Component({
  selector: 'jhi-time-off-calendar',
  templateUrl: './time-off-calendar.component.html',
  styleUrls: ['./time-off-calendar.component.scss'],
})
export class TimeOffCalendarComponent implements OnInit {
  eventss: any[] = [];
  isAdmin: any = null;
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: this.eventss,
  };
  private userId: number | null | undefined;
  private username: string | null | undefined;

  constructor(
    private timeOffRequestService: TimeOffRequestService,
    private accountService: AccountService,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.load();
  }

  handleDateSelection(info: any) {
    const start = info.startStr; // Start date of the selection
    const end = info.endStr; // End date of the selection

    console.log('Selected start date: ', start);
    console.log('Selected end date: ', end);

    // Create your event or perform any other action here
    const newEvent = { title: 'New Event', start: start, end: end };
    // this.calendarOptions.events.push(newEvent);
  }

  addEvent($event: any) {
    console.log($event);
    const modalRef = this.modalService.open(TimeOffRequestDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userId = this.userId;
    modalRef.componentInstance.date = $event.date;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.load();
    });
  }

  private load() {
    this.accountService.identity().subscribe(value => {
      this.userId = value?.id;
      this.isAdmin = value?.authorities.includes('ROLE_ADMIN');
      this.username = (value?.firstName || value?.login || 'John') + (value?.lastName || value?.login || 'Doe');
      this.getRequests();
    });
  }

  private getRequests() {
    const queryObject: any = {
      page: 0,
      size: 100,
      'userId.equals': this.userId,
    };
    this.eventss = [];
    this.timeOffRequestService.query(queryObject).subscribe(value => {
      if (value.body) {
        console.log(value.body);
        for (const iTimeOffRequest of value.body) {
          this.eventss.push({
            title: iTimeOffRequest.leaveReason + ';' + iTimeOffRequest.status + ';' + iTimeOffRequest.id,
            start: iTimeOffRequest.startDate?.toDate(),
            end: iTimeOffRequest.endDate?.toDate(),
          });
          console.log('for', this.eventss);
        }
        this.calendarOptions.events = [...this.eventss];
      }
    });
  }

  removeEvent(string: string) {
    this.timeOffRequestService.delete(Number(string)).subscribe(value => {
      this.load();
    });
  }
}
