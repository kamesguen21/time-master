import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITimeOffRequest } from '../time-off-request.model';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { TimeOffRequestStatus } from '../../enumerations/time-off-request-status.model';

@Component({
  templateUrl: './time-off-request-delete-dialog.component.html',
})
export class TimeOffRequestDeleteDialogComponent {
  timeOffRequest?: ITimeOffRequest;
  title?: string;
  message?: string;

  constructor(protected timeOffRequestService: TimeOffRequestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.timeOffRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }

  confirmAction(number: number) {
    if (this.timeOffRequest) {
      if (this.title === 'Rejection') {
        this.timeOffRequest.status = TimeOffRequestStatus.REJECTED;
      } else {
        this.timeOffRequest.status = TimeOffRequestStatus.APPROVED;
      }
      this.timeOffRequestService.update(this.timeOffRequest).subscribe(() => {
        this.activeModal.close(ITEM_DELETED_EVENT);
      });
    } else {
      this.activeModal.close(ITEM_DELETED_EVENT);
    }
  }
}
