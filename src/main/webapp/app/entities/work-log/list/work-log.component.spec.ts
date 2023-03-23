import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { WorkLogService } from '../service/work-log.service';

import { WorkLogComponent } from './work-log.component';

describe('WorkLog Management Component', () => {
  let comp: WorkLogComponent;
  let fixture: ComponentFixture<WorkLogComponent>;
  let service: WorkLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'work-log', component: WorkLogComponent }]), HttpClientTestingModule],
      declarations: [WorkLogComponent],
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
      .overrideTemplate(WorkLogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkLogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(WorkLogService);

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
    expect(comp.workLogs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to workLogService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getWorkLogIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getWorkLogIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
