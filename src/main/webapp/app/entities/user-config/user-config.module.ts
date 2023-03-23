import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserConfigComponent } from './list/user-config.component';
import { UserConfigDetailComponent } from './detail/user-config-detail.component';
import { UserConfigUpdateComponent } from './update/user-config-update.component';
import { UserConfigDeleteDialogComponent } from './delete/user-config-delete-dialog.component';
import { UserConfigRoutingModule } from './route/user-config-routing.module';

@NgModule({
  imports: [SharedModule, UserConfigRoutingModule],
  declarations: [UserConfigComponent, UserConfigDetailComponent, UserConfigUpdateComponent, UserConfigDeleteDialogComponent],
})
export class UserConfigModule {}
