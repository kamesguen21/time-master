import dayjs from 'dayjs/esm';

import { TimeOffRequestStatus } from 'app/entities/enumerations/time-off-request-status.model';

import { ITimeOffRequest, NewTimeOffRequest } from './time-off-request.model';

export const sampleWithRequiredData: ITimeOffRequest = {
  id: 43961,
  startDate: dayjs('2023-05-14T00:15'),
  endDate: dayjs('2023-05-14T07:54'),
};

export const sampleWithPartialData: ITimeOffRequest = {
  id: 90663,
  startDate: dayjs('2023-05-14T11:42'),
  endDate: dayjs('2023-05-14T07:21'),
  leaveReason: 'Pound Burundi',
};

export const sampleWithFullData: ITimeOffRequest = {
  id: 28305,
  startDate: dayjs('2023-05-13T18:15'),
  endDate: dayjs('2023-05-14T02:31'),
  status: TimeOffRequestStatus['REJECTED'],
  userId: 59852,
  leaveReason: 'Soft',
};

export const sampleWithNewData: NewTimeOffRequest = {
  startDate: dayjs('2023-05-14T01:54'),
  endDate: dayjs('2023-05-13T20:04'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
