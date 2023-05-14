import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { WorkLogBoardModule } from '../entities/work-log-board/work-log-board.module';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), WorkLogBoardModule],
  declarations: [HomeComponent],
})
export class HomeModule {}
