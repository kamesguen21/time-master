import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'user-config',
        data: { pageTitle: 'UserConfigs' },
        loadChildren: () => import('./user-config/user-config.module').then(m => m.UserConfigModule),
      },
      {
        path: 'ticket',
        data: { pageTitle: 'Tickets' },
        loadChildren: () => import('./ticket/ticket.module').then(m => m.TicketModule),
      },
      {
        path: 'work-log',
        data: { pageTitle: 'WorkLogs' },
        loadChildren: () => import('./work-log/work-log.module').then(m => m.WorkLogModule),
      },
      {
        path: 'time-off-request',
        data: { pageTitle: 'TimeOffRequests' },
        loadChildren: () => import('./time-off-request/time-off-request.module').then(m => m.TimeOffRequestModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
