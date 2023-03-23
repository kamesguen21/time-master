import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITimeOffRequest, NewTimeOffRequest } from '../time-off-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimeOffRequest for edit and NewTimeOffRequestFormGroupInput for create.
 */
type TimeOffRequestFormGroupInput = ITimeOffRequest | PartialWithRequiredKeyOf<NewTimeOffRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITimeOffRequest | NewTimeOffRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type TimeOffRequestFormRawValue = FormValueOf<ITimeOffRequest>;

type NewTimeOffRequestFormRawValue = FormValueOf<NewTimeOffRequest>;

type TimeOffRequestFormDefaults = Pick<NewTimeOffRequest, 'id' | 'startDate' | 'endDate'>;

type TimeOffRequestFormGroupContent = {
  id: FormControl<TimeOffRequestFormRawValue['id'] | NewTimeOffRequest['id']>;
  startDate: FormControl<TimeOffRequestFormRawValue['startDate']>;
  endDate: FormControl<TimeOffRequestFormRawValue['endDate']>;
  status: FormControl<TimeOffRequestFormRawValue['status']>;
  user: FormControl<TimeOffRequestFormRawValue['user']>;
};

export type TimeOffRequestFormGroup = FormGroup<TimeOffRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimeOffRequestFormService {
  createTimeOffRequestFormGroup(timeOffRequest: TimeOffRequestFormGroupInput = { id: null }): TimeOffRequestFormGroup {
    const timeOffRequestRawValue = this.convertTimeOffRequestToTimeOffRequestRawValue({
      ...this.getFormDefaults(),
      ...timeOffRequest,
    });
    return new FormGroup<TimeOffRequestFormGroupContent>({
      id: new FormControl(
        { value: timeOffRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      startDate: new FormControl(timeOffRequestRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(timeOffRequestRawValue.endDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(timeOffRequestRawValue.status),
      user: new FormControl(timeOffRequestRawValue.user),
    });
  }

  getTimeOffRequest(form: TimeOffRequestFormGroup): ITimeOffRequest | NewTimeOffRequest {
    return this.convertTimeOffRequestRawValueToTimeOffRequest(
      form.getRawValue() as TimeOffRequestFormRawValue | NewTimeOffRequestFormRawValue
    );
  }

  resetForm(form: TimeOffRequestFormGroup, timeOffRequest: TimeOffRequestFormGroupInput): void {
    const timeOffRequestRawValue = this.convertTimeOffRequestToTimeOffRequestRawValue({ ...this.getFormDefaults(), ...timeOffRequest });
    form.reset(
      {
        ...timeOffRequestRawValue,
        id: { value: timeOffRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TimeOffRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertTimeOffRequestRawValueToTimeOffRequest(
    rawTimeOffRequest: TimeOffRequestFormRawValue | NewTimeOffRequestFormRawValue
  ): ITimeOffRequest | NewTimeOffRequest {
    return {
      ...rawTimeOffRequest,
      startDate: dayjs(rawTimeOffRequest.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawTimeOffRequest.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertTimeOffRequestToTimeOffRequestRawValue(
    timeOffRequest: ITimeOffRequest | (Partial<NewTimeOffRequest> & TimeOffRequestFormDefaults)
  ): TimeOffRequestFormRawValue | PartialWithRequiredKeyOf<NewTimeOffRequestFormRawValue> {
    return {
      ...timeOffRequest,
      startDate: timeOffRequest.startDate ? timeOffRequest.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: timeOffRequest.endDate ? timeOffRequest.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
