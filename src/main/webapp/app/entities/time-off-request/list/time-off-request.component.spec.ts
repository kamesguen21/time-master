import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TimeOffRequestService } from '../service/time-off-request.service';

import { TimeOffRequestComponent } from './time-off-request.component';

describe('TimeOffRequest Management Component', () => {
  let comp: TimeOffRequestComponent;
  let fixture: ComponentFixture<TimeOffRequestComponent>;
  let service: TimeOffRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'time-off-request', component: TimeOffRequestComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [TimeOffRequestComponent],
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
      .overrideTemplate(TimeOffRequestComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TimeOffRequestComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TimeOffRequestService);

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
    expect(comp.timeOffRequests?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to timeOffRequestService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTimeOffRequestIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTimeOffRequestIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
