import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TimeOffRequestComponent } from './list/time-off-request.component';
import { TimeOffRequestDetailComponent } from './detail/time-off-request-detail.component';
import { TimeOffRequestUpdateComponent } from './update/time-off-request-update.component';
import { TimeOffRequestDeleteDialogComponent } from './delete/time-off-request-delete-dialog.component';
import { TimeOffRequestRoutingModule } from './route/time-off-request-routing.module';

@NgModule({
  imports: [SharedModule, TimeOffRequestRoutingModule],
  declarations: [
    TimeOffRequestComponent,
    TimeOffRequestDetailComponent,
    TimeOffRequestUpdateComponent,
    TimeOffRequestDeleteDialogComponent,
  ],
})
export class TimeOffRequestModule {}
