import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserConfig } from '../user-config.model';

@Component({
  selector: 'jhi-user-config-detail',
  templateUrl: './user-config-detail.component.html',
})
export class UserConfigDetailComponent implements OnInit {
  userConfig: IUserConfig | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userConfig }) => {
      this.userConfig = userConfig;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
