import { Component, Input, OnInit } from '@angular/core';
import { ITimeOffRequest } from '../time-off-request.model';
import { TimeOffRequestStatus } from '../../enumerations/time-off-request-status.model';
import { TimeOffRequestFormGroup, TimeOffRequestFormService } from '../update/time-off-request-form.service';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-time-off-request-dialog',
  templateUrl: './time-off-request-dialog.component.html',
  styleUrls: ['./time-off-request-dialog.component.scss'],
})
export class TimeOffRequestDialogComponent implements OnInit {
  @Input() userId: number | undefined;
  @Input() date: any | undefined;

  isSaving = false;
  timeOffRequest: ITimeOffRequest | null = null;
  timeOffRequestStatusValues = Object.keys(TimeOffRequestStatus);

  editForm: TimeOffRequestFormGroup = this.timeOffRequestFormService.createTimeOffRequestFormGroup();

  constructor(
    protected timeOffRequestService: TimeOffRequestService,
    protected timeOffRequestFormService: TimeOffRequestFormService,
    protected activatedRoute: ActivatedRoute,
    protected activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeOffRequest }) => {
      this.timeOffRequest = timeOffRequest;
      if (timeOffRequest) {
        this.updateForm(timeOffRequest);
      }
    });
  }

  previousState(): void {
    this.close();
  }

  close(): void {
    this.activeModal.close('refresh');
  }

  save(): void {
    this.isSaving = true;
    const timeOffRequest = this.timeOffRequestFormService.getTimeOffRequest(this.editForm);
    timeOffRequest.userId = this.userId;
    timeOffRequest.status = TimeOffRequestStatus.PENDING;
    timeOffRequest.startDate = this.date;
    timeOffRequest.endDate = this.date;
    if (timeOffRequest.id !== null) {
      this.subscribeToSaveResponse(this.timeOffRequestService.update(timeOffRequest));
    } else {
      this.subscribeToSaveResponse(this.timeOffRequestService.create(timeOffRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITimeOffRequest>>): void {
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

  protected updateForm(timeOffRequest: ITimeOffRequest): void {
    this.timeOffRequest = timeOffRequest;
    this.timeOffRequestFormService.resetForm(this.editForm, timeOffRequest);
  }
}
