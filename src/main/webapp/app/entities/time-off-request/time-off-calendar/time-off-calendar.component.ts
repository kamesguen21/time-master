import { Component, OnInit } from '@angular/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/daygrid';
import { CalendarOptions } from '@fullcalendar/core';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { AccountService } from '../../../core/auth/account.service';
import { tap } from 'rxjs';

@Component({
  selector: 'jhi-time-off-calendar',
  templateUrl: './time-off-calendar.component.html',
  styleUrls: ['./time-off-calendar.component.scss'],
})
export class TimeOffCalendarComponent implements OnInit {
  // @ts-ignore
  eventss: any[] = [];
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: this.eventss,
  };
  private userId: number | null | undefined;
  private username: string | null | undefined;

  constructor(private timeOffRequestService: TimeOffRequestService, private accountService: AccountService) {}

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

  testt($event: any) {
    console.log($event);
  }

  private load() {
    this.accountService.identity().subscribe(value => {
      this.userId = value?.id;
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
    return this.timeOffRequestService.query(queryObject).subscribe(value => {
      if (value.body) {
        for (const iTimeOffRequest of value.body) {
          this.eventss.push({ title: 'Meeting', start: new Date() });
        }
      }
    });
  }
}
