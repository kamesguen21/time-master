import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TimeOffRequestFormService } from './time-off-request-form.service';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { ITimeOffRequest } from '../time-off-request.model';
import { IUserConfig } from 'app/entities/user-config/user-config.model';
import { UserConfigService } from 'app/entities/user-config/service/user-config.service';

import { TimeOffRequestUpdateComponent } from './time-off-request-update.component';

describe('TimeOffRequest Management Update Component', () => {
  let comp: TimeOffRequestUpdateComponent;
  let fixture: ComponentFixture<TimeOffRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let timeOffRequestFormService: TimeOffRequestFormService;
  let timeOffRequestService: TimeOffRequestService;
  let userConfigService: UserConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TimeOffRequestUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TimeOffRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TimeOffRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timeOffRequestFormService = TestBed.inject(TimeOffRequestFormService);
    timeOffRequestService = TestBed.inject(TimeOffRequestService);
    userConfigService = TestBed.inject(UserConfigService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserConfig query and add missing value', () => {
      const timeOffRequest: ITimeOffRequest = { id: 456 };
      const user: IUserConfig = { id: 38296 };
      timeOffRequest.user = user;

      const userConfigCollection: IUserConfig[] = [{ id: 17935 }];
      jest.spyOn(userConfigService, 'query').mockReturnValue(of(new HttpResponse({ body: userConfigCollection })));
      const additionalUserConfigs = [user];
      const expectedCollection: IUserConfig[] = [...additionalUserConfigs, ...userConfigCollection];
      jest.spyOn(userConfigService, 'addUserConfigToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timeOffRequest });
      comp.ngOnInit();

      expect(userConfigService.query).toHaveBeenCalled();
      expect(userConfigService.addUserConfigToCollectionIfMissing).toHaveBeenCalledWith(
        userConfigCollection,
        ...additionalUserConfigs.map(expect.objectContaining)
      );
      expect(comp.userConfigsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const timeOffRequest: ITimeOffRequest = { id: 456 };
      const user: IUserConfig = { id: 96220 };
      timeOffRequest.user = user;

      activatedRoute.data = of({ timeOffRequest });
      comp.ngOnInit();

      expect(comp.userConfigsSharedCollection).toContain(user);
      expect(comp.timeOffRequest).toEqual(timeOffRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOffRequest>>();
      const timeOffRequest = { id: 123 };
      jest.spyOn(timeOffRequestFormService, 'getTimeOffRequest').mockReturnValue(timeOffRequest);
      jest.spyOn(timeOffRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOffRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeOffRequest }));
      saveSubject.complete();

      // THEN
      expect(timeOffRequestFormService.getTimeOffRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timeOffRequestService.update).toHaveBeenCalledWith(expect.objectContaining(timeOffRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOffRequest>>();
      const timeOffRequest = { id: 123 };
      jest.spyOn(timeOffRequestFormService, 'getTimeOffRequest').mockReturnValue({ id: null });
      jest.spyOn(timeOffRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOffRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeOffRequest }));
      saveSubject.complete();

      // THEN
      expect(timeOffRequestFormService.getTimeOffRequest).toHaveBeenCalled();
      expect(timeOffRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOffRequest>>();
      const timeOffRequest = { id: 123 };
      jest.spyOn(timeOffRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOffRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timeOffRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserConfig', () => {
      it('Should forward to userConfigService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userConfigService, 'compareUserConfig');
        comp.compareUserConfig(entity, entity2);
        expect(userConfigService.compareUserConfig).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
