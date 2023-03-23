import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITimeOffRequest } from '../time-off-request.model';
import { TimeOffRequestService } from '../service/time-off-request.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './time-off-request-delete-dialog.component.html',
})
export class TimeOffRequestDeleteDialogComponent {
  timeOffRequest?: ITimeOffRequest;

  constructor(protected timeOffRequestService: TimeOffRequestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.timeOffRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
