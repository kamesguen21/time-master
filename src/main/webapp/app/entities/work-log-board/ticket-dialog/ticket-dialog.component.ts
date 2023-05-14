import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TicketStatus } from '../../enumerations/ticket-status.model';
import { ITicket } from '../../ticket/ticket.model';
import { TicketFormGroup, TicketFormService } from '../../ticket/update/ticket-form.service';
import { TicketService } from '../../ticket/service/ticket.service';
import { UserManagementService } from '../../../admin/user-management/service/user-management.service';
import { IUser } from '../../../admin/user-management/user-management.model';

@Component({
  selector: 'jhi-ticket-dialog',
  templateUrl: './ticket-dialog.component.html',
  styleUrls: ['./ticket-dialog.component.scss'],
})
export class TicketDialogComponent implements OnInit {
  @Input() userId: number | undefined;
  @Input() status: TicketStatus | undefined;

  isSaving = false;
  ticket: ITicket | null = null;
  ticketStatusValues = Object.keys(TicketStatus);
  users: IUser[] | null = null;

  editForm: TicketFormGroup = this.ticketFormService.createTicketFormGroup();

  constructor(
    protected ticketService: TicketService,
    protected activeModal: NgbActiveModal,
    protected userManagementService: UserManagementService,
    protected ticketFormService: TicketFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticket }) => {
      this.ticket = ticket;
      if (ticket) {
        this.updateForm(ticket);
      }
    });
    this.userManagementService.query().subscribe(value => {
      this.users = value.body;
    });
  }

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userManagementService.compareUser(o1, o2);

  close(): void {
    this.activeModal.close('refresh');
  }

  previousState(): void {
    this.close();
  }

  save(): void {
    this.isSaving = true;
    const ticket = this.ticketFormService.getTicket(this.editForm);
    ticket.userId = this.userId;
    ticket.status = this.status;
    if (ticket.id !== null) {
      this.subscribeToSaveResponse(this.ticketService.update(ticket));
    } else {
      this.subscribeToSaveResponse(this.ticketService.create(ticket));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITicket>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ticket: ITicket): void {
    this.ticket = ticket;
    this.ticketFormService.resetForm(this.editForm, ticket);
  }
}
