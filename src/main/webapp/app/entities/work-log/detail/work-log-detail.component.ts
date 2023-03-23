import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWorkLog } from '../work-log.model';

@Component({
  selector: 'jhi-work-log-detail',
  templateUrl: './work-log-detail.component.html',
})
export class WorkLogDetailComponent implements OnInit {
  workLog: IWorkLog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workLog }) => {
      this.workLog = workLog;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
