import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TimeOffRequestComponent } from '../list/time-off-request.component';
import { TimeOffRequestDetailComponent } from '../detail/time-off-request-detail.component';
import { TimeOffRequestUpdateComponent } from '../update/time-off-request-update.component';
import { TimeOffRequestRoutingResolveService } from './time-off-request-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';
import { TimeOffCalendarComponent } from '../time-off-calendar/time-off-calendar.component';

const timeOffRequestRoute: Routes = [
  {
    path: '',
    component: TimeOffCalendarComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TimeOffRequestDetailComponent,
    resolve: {
      timeOffRequest: TimeOffRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TimeOffRequestUpdateComponent,
    resolve: {
      timeOffRequest: TimeOffRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TimeOffRequestUpdateComponent,
    resolve: {
      timeOffRequest: TimeOffRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(timeOffRequestRoute)],
  exports: [RouterModule],
})
export class TimeOffRequestRoutingModule {}
