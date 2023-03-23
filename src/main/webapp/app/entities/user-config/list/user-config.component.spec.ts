import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserConfigService } from '../service/user-config.service';

import { UserConfigComponent } from './user-config.component';

describe('UserConfig Management Component', () => {
  let comp: UserConfigComponent;
  let fixture: ComponentFixture<UserConfigComponent>;
  let service: UserConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'user-config', component: UserConfigComponent }]), HttpClientTestingModule],
      declarations: [UserConfigComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(UserConfigComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserConfigComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserConfigService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.userConfigs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userConfigService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserConfigIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserConfigIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
