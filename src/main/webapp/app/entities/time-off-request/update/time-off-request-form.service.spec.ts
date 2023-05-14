import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../time-off-request.test-samples';

import { TimeOffRequestFormService } from './time-off-request-form.service';

describe('TimeOffRequest Form Service', () => {
  let service: TimeOffRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimeOffRequestFormService);
  });

  describe('Service methods', () => {
    describe('createTimeOffRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimeOffRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            userId: expect.any(Object),
          })
        );
      });

      it('passing ITimeOffRequest should create a new form with FormGroup', () => {
        const formGroup = service.createTimeOffRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            userId: expect.any(Object),
          })
        );
      });
    });

    describe('getTimeOffRequest', () => {
      it('should return NewTimeOffRequest for default TimeOffRequest initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTimeOffRequestFormGroup(sampleWithNewData);

        const timeOffRequest = service.getTimeOffRequest(formGroup) as any;

        expect(timeOffRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimeOffRequest for empty TimeOffRequest initial value', () => {
        const formGroup = service.createTimeOffRequestFormGroup();

        const timeOffRequest = service.getTimeOffRequest(formGroup) as any;

        expect(timeOffRequest).toMatchObject({});
      });

      it('should return ITimeOffRequest', () => {
        const formGroup = service.createTimeOffRequestFormGroup(sampleWithRequiredData);

        const timeOffRequest = service.getTimeOffRequest(formGroup) as any;

        expect(timeOffRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimeOffRequest should not enable id FormControl', () => {
        const formGroup = service.createTimeOffRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimeOffRequest should disable id FormControl', () => {
        const formGroup = service.createTimeOffRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
