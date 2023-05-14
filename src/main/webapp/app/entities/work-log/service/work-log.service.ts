import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWorkLog, NewWorkLog } from '../work-log.model';

export type PartialUpdateWorkLog = Partial<IWorkLog> & Pick<IWorkLog, 'id'>;

type RestOf<T extends IWorkLog | NewWorkLog> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestWorkLog = RestOf<IWorkLog>;

export type NewRestWorkLog = RestOf<NewWorkLog>;

export type PartialUpdateRestWorkLog = RestOf<PartialUpdateWorkLog>;

export type EntityResponseType = HttpResponse<IWorkLog>;
export type EntityArrayResponseType = HttpResponse<IWorkLog[]>;

@Injectable({ providedIn: 'root' })
export class WorkLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/work-logs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(workLog: NewWorkLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workLog);
    return this.http
      .post<RestWorkLog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(workLog: IWorkLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workLog);
    return this.http
      .put<RestWorkLog>(`${this.resourceUrl}/${this.getWorkLogIdentifier(workLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(workLog: PartialUpdateWorkLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workLog);
    return this.http
      .patch<RestWorkLog>(`${this.resourceUrl}/${this.getWorkLogIdentifier(workLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWorkLog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWorkLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWorkLogIdentifier(workLog: Pick<IWorkLog, 'id'>): number {
    return workLog.id;
  }

  compareWorkLog(o1: Pick<IWorkLog, 'id'> | null, o2: Pick<IWorkLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getWorkLogIdentifier(o1) === this.getWorkLogIdentifier(o2) : o1 === o2;
  }

  addWorkLogToCollectionIfMissing<Type extends Pick<IWorkLog, 'id'>>(
    workLogCollection: Type[],
    ...workLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const workLogs: Type[] = workLogsToCheck.filter(isPresent);
    if (workLogs.length > 0) {
      const workLogCollectionIdentifiers = workLogCollection.map(workLogItem => this.getWorkLogIdentifier(workLogItem)!);
      const workLogsToAdd = workLogs.filter(workLogItem => {
        const workLogIdentifier = this.getWorkLogIdentifier(workLogItem);
        if (workLogCollectionIdentifiers.includes(workLogIdentifier)) {
          return false;
        }
        workLogCollectionIdentifiers.push(workLogIdentifier);
        return true;
      });
      return [...workLogsToAdd, ...workLogCollection];
    }
    return workLogCollection;
  }

  protected convertDateFromClient<T extends IWorkLog | NewWorkLog | PartialUpdateWorkLog>(workLog: T): RestOf<T> {
    return {
      ...workLog,
      date: workLog.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restWorkLog: RestWorkLog): IWorkLog {
    return {
      ...restWorkLog,
      date: restWorkLog.date ? dayjs(restWorkLog.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWorkLog>): HttpResponse<IWorkLog> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWorkLog[]>): HttpResponse<IWorkLog[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
