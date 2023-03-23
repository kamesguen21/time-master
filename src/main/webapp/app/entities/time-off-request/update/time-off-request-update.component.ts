import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TimeOffRequestFormService, TimeOffRequestFormGroup } from './time-off-request-form.service';
import { ITimeOffRequest } from '../time-off-request.model';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { IUserConfig } from 'app/entities/user-config/user-config.model';
import { UserConfigService } from 'app/entities/user-config/service/user-config.service';

@Component({
  selector: 'jhi-time-off-request-update',
  templateUrl: './time-off-request-update.component.html',
})
export class TimeOffRequestUpdateComponent implements OnInit {
  isSaving = false;
  timeOffRequest: ITimeOffRequest | null = null;

  userConfigsSharedCollection: IUserConfig[] = [];

  editForm: TimeOffRequestFormGroup = this.timeOffRequestFormService.createTimeOffRequestFormGroup();

  constructor(
    protected timeOffRequestService: TimeOffRequestService,
    protected timeOffRequestFormService: TimeOffRequestFormService,
    protected userConfigService: UserConfigService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUserConfig = (o1: IUserConfig | null, o2: IUserConfig | null): boolean => this.userConfigService.compareUserConfig(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeOffRequest }) => {
      this.timeOffRequest = timeOffRequest;
      if (timeOffRequest) {
        this.updateForm(timeOffRequest);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const timeOffRequest = this.timeOffRequestFormService.getTimeOffRequest(this.editForm);
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

    this.userConfigsSharedCollection = this.userConfigService.addUserConfigToCollectionIfMissing<IUserConfig>(
      this.userConfigsSharedCollection,
      timeOffRequest.user
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userConfigService
      .query()
      .pipe(map((res: HttpResponse<IUserConfig[]>) => res.body ?? []))
      .pipe(
        map((userConfigs: IUserConfig[]) =>
          this.userConfigService.addUserConfigToCollectionIfMissing<IUserConfig>(userConfigs, this.timeOffRequest?.user)
        )
      )
      .subscribe((userConfigs: IUserConfig[]) => (this.userConfigsSharedCollection = userConfigs));
  }
}
