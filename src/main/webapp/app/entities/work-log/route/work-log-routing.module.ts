import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WorkLogComponent } from '../list/work-log.component';
import { WorkLogDetailComponent } from '../detail/work-log-detail.component';
import { WorkLogUpdateComponent } from '../update/work-log-update.component';
import { WorkLogRoutingResolveService } from './work-log-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const workLogRoute: Routes = [
  {
    path: '',
    component: WorkLogComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WorkLogDetailComponent,
    resolve: {
      workLog: WorkLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WorkLogUpdateComponent,
    resolve: {
      workLog: WorkLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WorkLogUpdateComponent,
    resolve: {
      workLog: WorkLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(workLogRoute)],
  exports: [RouterModule],
})
export class WorkLogRoutingModule {}
