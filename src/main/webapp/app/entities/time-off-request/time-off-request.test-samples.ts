import dayjs from 'dayjs/esm';

import { ITimeOffRequest, NewTimeOffRequest } from './time-off-request.model';

export const sampleWithRequiredData: ITimeOffRequest = {
  id: 43961,
  startDate: dayjs('2023-05-14T00:15'),
  endDate: dayjs('2023-05-14T07:54'),
};

export const sampleWithPartialData: ITimeOffRequest = {
  id: 76272,
  startDate: dayjs('2023-05-13T16:32'),
  endDate: dayjs('2023-05-14T11:42'),
};

export const sampleWithFullData: ITimeOffRequest = {
  id: 28956,
  startDate: dayjs('2023-05-14T00:38'),
  endDate: dayjs('2023-05-13T21:42'),
  status: 'haptic Response Cambridgeshire',
  userId: 887,
};

export const sampleWithNewData: NewTimeOffRequest = {
  startDate: dayjs('2023-05-14T10:48'),
  endDate: dayjs('2023-05-13T19:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
