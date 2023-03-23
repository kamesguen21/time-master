import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITimeOffRequest } from '../time-off-request.model';

@Component({
  selector: 'jhi-time-off-request-detail',
  templateUrl: './time-off-request-detail.component.html',
})
export class TimeOffRequestDetailComponent implements OnInit {
  timeOffRequest: ITimeOffRequest | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeOffRequest }) => {
      this.timeOffRequest = timeOffRequest;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
