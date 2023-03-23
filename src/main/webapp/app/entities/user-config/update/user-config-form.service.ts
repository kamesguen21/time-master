import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IUserConfig, NewUserConfig } from '../user-config.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserConfig for edit and NewUserConfigFormGroupInput for create.
 */
type UserConfigFormGroupInput = IUserConfig | PartialWithRequiredKeyOf<NewUserConfig>;

type UserConfigFormDefaults = Pick<NewUserConfig, 'id'>;

type UserConfigFormGroupContent = {
  id: FormControl<IUserConfig['id'] | NewUserConfig['id']>;
  login: FormControl<IUserConfig['login']>;
  firstName: FormControl<IUserConfig['firstName']>;
  lastName: FormControl<IUserConfig['lastName']>;
  email: FormControl<IUserConfig['email']>;
  phoneNumber: FormControl<IUserConfig['phoneNumber']>;
};

export type UserConfigFormGroup = FormGroup<UserConfigFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserConfigFormService {
  createUserConfigFormGroup(userConfig: UserConfigFormGroupInput = { id: null }): UserConfigFormGroup {
    const userConfigRawValue = {
      ...this.getFormDefaults(),
      ...userConfig,
    };
    return new FormGroup<UserConfigFormGroupContent>({
      id: new FormControl(
        { value: userConfigRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      login: new FormControl(userConfigRawValue.login, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(userConfigRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(userConfigRawValue.lastName, {
        validators: [Validators.required],
      }),
      email: new FormControl(userConfigRawValue.email, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(userConfigRawValue.phoneNumber, {
        validators: [Validators.required],
      }),
    });
  }

  getUserConfig(form: UserConfigFormGroup): IUserConfig | NewUserConfig {
    return form.getRawValue() as IUserConfig | NewUserConfig;
  }

  resetForm(form: UserConfigFormGroup, userConfig: UserConfigFormGroupInput): void {
    const userConfigRawValue = { ...this.getFormDefaults(), ...userConfig };
    form.reset(
      {
        ...userConfigRawValue,
        id: { value: userConfigRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserConfigFormDefaults {
    return {
      id: null,
    };
  }
}
