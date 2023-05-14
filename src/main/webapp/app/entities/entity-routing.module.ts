import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ticket',
        data: { pageTitle: 'Tickets' },
        loadChildren: () => import('./ticket/ticket.module').then(m => m.TicketModule),
      },
      {
        path: 'time-off-request',
        data: { pageTitle: 'TimeOffRequests' },
        loadChildren: () => import('./time-off-request/time-off-request.module').then(m => m.TimeOffRequestModule),
      },
      {
        path: 'work-log',
        data: { pageTitle: 'WorkLogs' },
        loadChildren: () => import('./work-log/work-log.module').then(m => m.WorkLogModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
