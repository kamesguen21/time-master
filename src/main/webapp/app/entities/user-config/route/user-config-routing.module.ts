import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserConfigComponent } from '../list/user-config.component';
import { UserConfigDetailComponent } from '../detail/user-config-detail.component';
import { UserConfigUpdateComponent } from '../update/user-config-update.component';
import { UserConfigRoutingResolveService } from './user-config-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userConfigRoute: Routes = [
  {
    path: '',
    component: UserConfigComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserConfigDetailComponent,
    resolve: {
      userConfig: UserConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserConfigUpdateComponent,
    resolve: {
      userConfig: UserConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserConfigUpdateComponent,
    resolve: {
      userConfig: UserConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userConfigRoute)],
  exports: [RouterModule],
})
export class UserConfigRoutingModule {}
