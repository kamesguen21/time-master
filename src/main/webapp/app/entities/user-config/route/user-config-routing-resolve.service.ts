import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserConfig } from '../user-config.model';
import { UserConfigService } from '../service/user-config.service';

@Injectable({ providedIn: 'root' })
export class UserConfigRoutingResolveService implements Resolve<IUserConfig | null> {
  constructor(protected service: UserConfigService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserConfig | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userConfig: HttpResponse<IUserConfig>) => {
          if (userConfig.body) {
            return of(userConfig.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
