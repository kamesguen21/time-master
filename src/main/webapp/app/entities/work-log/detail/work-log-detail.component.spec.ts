import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WorkLogDetailComponent } from './work-log-detail.component';

describe('WorkLog Management Detail Component', () => {
  let comp: WorkLogDetailComponent;
  let fixture: ComponentFixture<WorkLogDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WorkLogDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ workLog: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(WorkLogDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WorkLogDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load workLog on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.workLog).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
