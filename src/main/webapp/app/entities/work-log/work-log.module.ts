import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WorkLogComponent } from './list/work-log.component';
import { WorkLogDetailComponent } from './detail/work-log-detail.component';
import { WorkLogUpdateComponent } from './update/work-log-update.component';
import { WorkLogDeleteDialogComponent } from './delete/work-log-delete-dialog.component';
import { WorkLogRoutingModule } from './route/work-log-routing.module';

@NgModule({
  imports: [SharedModule, WorkLogRoutingModule],
  declarations: [WorkLogComponent, WorkLogDetailComponent, WorkLogUpdateComponent, WorkLogDeleteDialogComponent],
})
export class WorkLogModule {}
