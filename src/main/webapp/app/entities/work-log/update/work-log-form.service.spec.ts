import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../work-log.test-samples';

import { WorkLogFormService } from './work-log-form.service';

describe('WorkLog Form Service', () => {
  let service: WorkLogFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkLogFormService);
  });

  describe('Service methods', () => {
    describe('createWorkLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWorkLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timeSpent: expect.any(Object),
            date: expect.any(Object),
            user: expect.any(Object),
            ticket: expect.any(Object),
          })
        );
      });

      it('passing IWorkLog should create a new form with FormGroup', () => {
        const formGroup = service.createWorkLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timeSpent: expect.any(Object),
            date: expect.any(Object),
            user: expect.any(Object),
            ticket: expect.any(Object),
          })
        );
      });
    });

    describe('getWorkLog', () => {
      it('should return NewWorkLog for default WorkLog initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createWorkLogFormGroup(sampleWithNewData);

        const workLog = service.getWorkLog(formGroup) as any;

        expect(workLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewWorkLog for empty WorkLog initial value', () => {
        const formGroup = service.createWorkLogFormGroup();

        const workLog = service.getWorkLog(formGroup) as any;

        expect(workLog).toMatchObject({});
      });

      it('should return IWorkLog', () => {
        const formGroup = service.createWorkLogFormGroup(sampleWithRequiredData);

        const workLog = service.getWorkLog(formGroup) as any;

        expect(workLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWorkLog should not enable id FormControl', () => {
        const formGroup = service.createWorkLogFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWorkLog should disable id FormControl', () => {
        const formGroup = service.createWorkLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
