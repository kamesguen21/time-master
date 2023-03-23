import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserConfigFormService } from './user-config-form.service';
import { UserConfigService } from '../service/user-config.service';
import { IUserConfig } from '../user-config.model';

import { UserConfigUpdateComponent } from './user-config-update.component';

describe('UserConfig Management Update Component', () => {
  let comp: UserConfigUpdateComponent;
  let fixture: ComponentFixture<UserConfigUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userConfigFormService: UserConfigFormService;
  let userConfigService: UserConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserConfigUpdateComponent],
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
      .overrideTemplate(UserConfigUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserConfigUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userConfigFormService = TestBed.inject(UserConfigFormService);
    userConfigService = TestBed.inject(UserConfigService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const userConfig: IUserConfig = { id: 456 };

      activatedRoute.data = of({ userConfig });
      comp.ngOnInit();

      expect(comp.userConfig).toEqual(userConfig);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserConfig>>();
      const userConfig = { id: 123 };
      jest.spyOn(userConfigFormService, 'getUserConfig').mockReturnValue(userConfig);
      jest.spyOn(userConfigService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userConfig }));
      saveSubject.complete();

      // THEN
      expect(userConfigFormService.getUserConfig).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userConfigService.update).toHaveBeenCalledWith(expect.objectContaining(userConfig));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserConfig>>();
      const userConfig = { id: 123 };
      jest.spyOn(userConfigFormService, 'getUserConfig').mockReturnValue({ id: null });
      jest.spyOn(userConfigService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userConfig: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userConfig }));
      saveSubject.complete();

      // THEN
      expect(userConfigFormService.getUserConfig).toHaveBeenCalled();
      expect(userConfigService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserConfig>>();
      const userConfig = { id: 123 };
      jest.spyOn(userConfigService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userConfigService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
