import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TimeOffRequestDetailComponent } from './time-off-request-detail.component';

describe('TimeOffRequest Management Detail Component', () => {
  let comp: TimeOffRequestDetailComponent;
  let fixture: ComponentFixture<TimeOffRequestDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TimeOffRequestDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ timeOffRequest: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TimeOffRequestDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TimeOffRequestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load timeOffRequest on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.timeOffRequest).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
