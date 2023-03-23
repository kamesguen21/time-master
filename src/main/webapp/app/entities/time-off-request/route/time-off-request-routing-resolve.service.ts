import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITimeOffRequest } from '../time-off-request.model';
import { TimeOffRequestService } from '../service/time-off-request.service';

@Injectable({ providedIn: 'root' })
export class TimeOffRequestRoutingResolveService implements Resolve<ITimeOffRequest | null> {
  constructor(protected service: TimeOffRequestService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITimeOffRequest | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((timeOffRequest: HttpResponse<ITimeOffRequest>) => {
          if (timeOffRequest.body) {
            return of(timeOffRequest.body);
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
