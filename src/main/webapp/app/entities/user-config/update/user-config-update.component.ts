import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { UserConfigFormService, UserConfigFormGroup } from './user-config-form.service';
import { IUserConfig } from '../user-config.model';
import { UserConfigService } from '../service/user-config.service';

@Component({
  selector: 'jhi-user-config-update',
  templateUrl: './user-config-update.component.html',
})
export class UserConfigUpdateComponent implements OnInit {
  isSaving = false;
  userConfig: IUserConfig | null = null;

  editForm: UserConfigFormGroup = this.userConfigFormService.createUserConfigFormGroup();

  constructor(
    protected userConfigService: UserConfigService,
    protected userConfigFormService: UserConfigFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userConfig }) => {
      this.userConfig = userConfig;
      if (userConfig) {
        this.updateForm(userConfig);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userConfig = this.userConfigFormService.getUserConfig(this.editForm);
    if (userConfig.id !== null) {
      this.subscribeToSaveResponse(this.userConfigService.update(userConfig));
    } else {
      this.subscribeToSaveResponse(this.userConfigService.create(userConfig));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserConfig>>): void {
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

  protected updateForm(userConfig: IUserConfig): void {
    this.userConfig = userConfig;
    this.userConfigFormService.resetForm(this.editForm, userConfig);
  }
}
