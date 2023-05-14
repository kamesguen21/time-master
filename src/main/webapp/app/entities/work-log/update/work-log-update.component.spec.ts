import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WorkLogFormService } from './work-log-form.service';
import { WorkLogService } from '../service/work-log.service';
import { IWorkLog } from '../work-log.model';
import { ITicket } from 'app/entities/ticket/ticket.model';
import { TicketService } from 'app/entities/ticket/service/ticket.service';

import { WorkLogUpdateComponent } from './work-log-update.component';

describe('WorkLog Management Update Component', () => {
  let comp: WorkLogUpdateComponent;
  let fixture: ComponentFixture<WorkLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let workLogFormService: WorkLogFormService;
  let workLogService: WorkLogService;
  let ticketService: TicketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WorkLogUpdateComponent],
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
      .overrideTemplate(WorkLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    workLogFormService = TestBed.inject(WorkLogFormService);
    workLogService = TestBed.inject(WorkLogService);
    ticketService = TestBed.inject(TicketService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ticket query and add missing value', () => {
      const workLog: IWorkLog = { id: 456 };
      const ticket: ITicket = { id: 97870 };
      workLog.ticket = ticket;

      const ticketCollection: ITicket[] = [{ id: 63623 }];
      jest.spyOn(ticketService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketCollection })));
      const additionalTickets = [ticket];
      const expectedCollection: ITicket[] = [...additionalTickets, ...ticketCollection];
      jest.spyOn(ticketService, 'addTicketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      expect(ticketService.query).toHaveBeenCalled();
      expect(ticketService.addTicketToCollectionIfMissing).toHaveBeenCalledWith(
        ticketCollection,
        ...additionalTickets.map(expect.objectContaining)
      );
      expect(comp.ticketsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const workLog: IWorkLog = { id: 456 };
      const ticket: ITicket = { id: 2238 };
      workLog.ticket = ticket;

      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      expect(comp.ticketsSharedCollection).toContain(ticket);
      expect(comp.workLog).toEqual(workLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkLog>>();
      const workLog = { id: 123 };
      jest.spyOn(workLogFormService, 'getWorkLog').mockReturnValue(workLog);
      jest.spyOn(workLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workLog }));
      saveSubject.complete();

      // THEN
      expect(workLogFormService.getWorkLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(workLogService.update).toHaveBeenCalledWith(expect.objectContaining(workLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkLog>>();
      const workLog = { id: 123 };
      jest.spyOn(workLogFormService, 'getWorkLog').mockReturnValue({ id: null });
      jest.spyOn(workLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workLog }));
      saveSubject.complete();

      // THEN
      expect(workLogFormService.getWorkLog).toHaveBeenCalled();
      expect(workLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkLog>>();
      const workLog = { id: 123 };
      jest.spyOn(workLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(workLogService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTicket', () => {
      it('Should forward to ticketService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(ticketService, 'compareTicket');
        comp.compareTicket(entity, entity2);
        expect(ticketService.compareTicket).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
