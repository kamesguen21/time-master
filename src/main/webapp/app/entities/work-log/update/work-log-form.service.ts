import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWorkLog, NewWorkLog } from '../work-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWorkLog for edit and NewWorkLogFormGroupInput for create.
 */
type WorkLogFormGroupInput = IWorkLog | PartialWithRequiredKeyOf<NewWorkLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWorkLog | NewWorkLog> = Omit<T, 'date'> & {
  date?: string | null;
};

type WorkLogFormRawValue = FormValueOf<IWorkLog>;

type NewWorkLogFormRawValue = FormValueOf<NewWorkLog>;

type WorkLogFormDefaults = Pick<NewWorkLog, 'id' | 'date'>;

type WorkLogFormGroupContent = {
  id: FormControl<WorkLogFormRawValue['id'] | NewWorkLog['id']>;
  timeSpent: FormControl<WorkLogFormRawValue['timeSpent']>;
  date: FormControl<WorkLogFormRawValue['date']>;
  userId: FormControl<WorkLogFormRawValue['userId']>;
  ticket: FormControl<WorkLogFormRawValue['ticket']>;
};

export type WorkLogFormGroup = FormGroup<WorkLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WorkLogFormService {
  createWorkLogFormGroup(workLog: WorkLogFormGroupInput = { id: null }): WorkLogFormGroup {
    const workLogRawValue = this.convertWorkLogToWorkLogRawValue({
      ...this.getFormDefaults(),
      ...workLog,
    });
    return new FormGroup<WorkLogFormGroupContent>({
      id: new FormControl(
        { value: workLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      timeSpent: new FormControl(workLogRawValue.timeSpent, {
        validators: [Validators.required],
      }),
      date: new FormControl(workLogRawValue.date, {
        validators: [Validators.required],
      }),
      userId: new FormControl(workLogRawValue.userId),
      ticket: new FormControl(workLogRawValue.ticket),
    });
  }

  getWorkLog(form: WorkLogFormGroup): IWorkLog | NewWorkLog {
    return this.convertWorkLogRawValueToWorkLog(form.getRawValue() as WorkLogFormRawValue | NewWorkLogFormRawValue);
  }

  resetForm(form: WorkLogFormGroup, workLog: WorkLogFormGroupInput): void {
    const workLogRawValue = this.convertWorkLogToWorkLogRawValue({ ...this.getFormDefaults(), ...workLog });
    form.reset(
      {
        ...workLogRawValue,
        id: { value: workLogRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): WorkLogFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertWorkLogRawValueToWorkLog(rawWorkLog: WorkLogFormRawValue | NewWorkLogFormRawValue): IWorkLog | NewWorkLog {
    return {
      ...rawWorkLog,
      date: dayjs(rawWorkLog.date, DATE_TIME_FORMAT),
    };
  }

  private convertWorkLogToWorkLogRawValue(
    workLog: IWorkLog | (Partial<NewWorkLog> & WorkLogFormDefaults)
  ): WorkLogFormRawValue | PartialWithRequiredKeyOf<NewWorkLogFormRawValue> {
    return {
      ...workLog,
      date: workLog.date ? workLog.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
