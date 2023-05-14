import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { WorkLogFormService, WorkLogFormGroup } from './work-log-form.service';
import { IWorkLog } from '../work-log.model';
import { WorkLogService } from '../service/work-log.service';
import { ITicket } from 'app/entities/ticket/ticket.model';
import { TicketService } from 'app/entities/ticket/service/ticket.service';

@Component({
  selector: 'jhi-work-log-update',
  templateUrl: './work-log-update.component.html',
})
export class WorkLogUpdateComponent implements OnInit {
  isSaving = false;
  workLog: IWorkLog | null = null;

  ticketsSharedCollection: ITicket[] = [];

  editForm: WorkLogFormGroup = this.workLogFormService.createWorkLogFormGroup();

  constructor(
    protected workLogService: WorkLogService,
    protected workLogFormService: WorkLogFormService,
    protected ticketService: TicketService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTicket = (o1: ITicket | null, o2: ITicket | null): boolean => this.ticketService.compareTicket(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workLog }) => {
      this.workLog = workLog;
      if (workLog) {
        this.updateForm(workLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const workLog = this.workLogFormService.getWorkLog(this.editForm);
    if (workLog.id !== null) {
      this.subscribeToSaveResponse(this.workLogService.update(workLog));
    } else {
      this.subscribeToSaveResponse(this.workLogService.create(workLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWorkLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(workLog: IWorkLog): void {
    this.workLog = workLog;
    this.workLogFormService.resetForm(this.editForm, workLog);

    this.ticketsSharedCollection = this.ticketService.addTicketToCollectionIfMissing<ITicket>(this.ticketsSharedCollection, workLog.ticket);
  }

  protected loadRelationshipsOptions(): void {
    this.ticketService
      .query()
      .pipe(map((res: HttpResponse<ITicket[]>) => res.body ?? []))
      .pipe(map((tickets: ITicket[]) => this.ticketService.addTicketToCollectionIfMissing<ITicket>(tickets, this.workLog?.ticket)))
      .subscribe((tickets: ITicket[]) => (this.ticketsSharedCollection = tickets));
  }
}
