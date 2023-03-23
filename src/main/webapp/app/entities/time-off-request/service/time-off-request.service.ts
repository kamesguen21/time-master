import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ITimeOffRequest, NewTimeOffRequest } from '../time-off-request.model';

export type PartialUpdateTimeOffRequest = Partial<ITimeOffRequest> & Pick<ITimeOffRequest, 'id'>;

type RestOf<T extends ITimeOffRequest | NewTimeOffRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestTimeOffRequest = RestOf<ITimeOffRequest>;

export type NewRestTimeOffRequest = RestOf<NewTimeOffRequest>;

export type PartialUpdateRestTimeOffRequest = RestOf<PartialUpdateTimeOffRequest>;

export type EntityResponseType = HttpResponse<ITimeOffRequest>;
export type EntityArrayResponseType = HttpResponse<ITimeOffRequest[]>;

@Injectable({ providedIn: 'root' })
export class TimeOffRequestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/time-off-requests');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/time-off-requests');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(timeOffRequest: NewTimeOffRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOffRequest);
    return this.http
      .post<RestTimeOffRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(timeOffRequest: ITimeOffRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOffRequest);
    return this.http
      .put<RestTimeOffRequest>(`${this.resourceUrl}/${this.getTimeOffRequestIdentifier(timeOffRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(timeOffRequest: PartialUpdateTimeOffRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOffRequest);
    return this.http
      .patch<RestTimeOffRequest>(`${this.resourceUrl}/${this.getTimeOffRequestIdentifier(timeOffRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTimeOffRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTimeOffRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTimeOffRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getTimeOffRequestIdentifier(timeOffRequest: Pick<ITimeOffRequest, 'id'>): number {
    return timeOffRequest.id;
  }

  compareTimeOffRequest(o1: Pick<ITimeOffRequest, 'id'> | null, o2: Pick<ITimeOffRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimeOffRequestIdentifier(o1) === this.getTimeOffRequestIdentifier(o2) : o1 === o2;
  }

  addTimeOffRequestToCollectionIfMissing<Type extends Pick<ITimeOffRequest, 'id'>>(
    timeOffRequestCollection: Type[],
    ...timeOffRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timeOffRequests: Type[] = timeOffRequestsToCheck.filter(isPresent);
    if (timeOffRequests.length > 0) {
      const timeOffRequestCollectionIdentifiers = timeOffRequestCollection.map(
        timeOffRequestItem => this.getTimeOffRequestIdentifier(timeOffRequestItem)!
      );
      const timeOffRequestsToAdd = timeOffRequests.filter(timeOffRequestItem => {
        const timeOffRequestIdentifier = this.getTimeOffRequestIdentifier(timeOffRequestItem);
        if (timeOffRequestCollectionIdentifiers.includes(timeOffRequestIdentifier)) {
          return false;
        }
        timeOffRequestCollectionIdentifiers.push(timeOffRequestIdentifier);
        return true;
      });
      return [...timeOffRequestsToAdd, ...timeOffRequestCollection];
    }
    return timeOffRequestCollection;
  }

  protected convertDateFromClient<T extends ITimeOffRequest | NewTimeOffRequest | PartialUpdateTimeOffRequest>(
    timeOffRequest: T
  ): RestOf<T> {
    return {
      ...timeOffRequest,
      startDate: timeOffRequest.startDate?.toJSON() ?? null,
      endDate: timeOffRequest.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTimeOffRequest: RestTimeOffRequest): ITimeOffRequest {
    return {
      ...restTimeOffRequest,
      startDate: restTimeOffRequest.startDate ? dayjs(restTimeOffRequest.startDate) : undefined,
      endDate: restTimeOffRequest.endDate ? dayjs(restTimeOffRequest.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTimeOffRequest>): HttpResponse<ITimeOffRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTimeOffRequest[]>): HttpResponse<ITimeOffRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
