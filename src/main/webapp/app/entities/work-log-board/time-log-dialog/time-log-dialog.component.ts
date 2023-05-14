import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { finalize, map } from 'rxjs/operators';
import { ITicket } from '../../ticket/ticket.model';
import { IWorkLog } from '../../work-log/work-log.model';
import { WorkLogFormGroup, WorkLogFormService } from '../../work-log/update/work-log-form.service';
import { WorkLogService } from '../../work-log/service/work-log.service';
import { TicketService } from '../../ticket/service/ticket.service';

@Component({
  selector: 'jhi-time-log-dialog',
  templateUrl: './time-log-dialog.component.html',
  styleUrls: ['./time-log-dialog.component.scss'],
})
export class TimeLogDialogComponent implements OnInit {
  @Input() ticket: ITicket | undefined;
  @Input() userId: number | undefined;
  isSaving = false;
  workLog: IWorkLog | null = null;
  ticketsSharedCollection: ITicket[] = [];
  editForm: WorkLogFormGroup = this.workLogFormService.createWorkLogFormGroup();

  constructor(
    protected workLogService: WorkLogService,
    protected workLogFormService: WorkLogFormService,
    protected ticketService: TicketService,
    protected activatedRoute: ActivatedRoute,
    protected activeModal: NgbActiveModal
  ) {}

  close(): void {
    this.activeModal.close('refresh');
  }

  compareTicket = (o1: ITicket | null, o2: ITicket | null): boolean => this.ticketService.compareTicket(o1, o2);

  ngOnInit(): void {}

  save(): void {
    this.isSaving = true;
    const workLog = this.workLogFormService.getWorkLog(this.editForm);
    workLog.userId = this.userId;
    workLog.ticket = this.ticket;
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
    this.close();
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
