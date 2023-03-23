import dayjs from 'dayjs/esm';

import { ITimeOffRequest, NewTimeOffRequest } from './time-off-request.model';

export const sampleWithRequiredData: ITimeOffRequest = {
  id: 43961,
  startDate: dayjs('2023-03-22T22:15'),
  endDate: dayjs('2023-03-23T05:55'),
};

export const sampleWithPartialData: ITimeOffRequest = {
  id: 24077,
  startDate: dayjs('2023-03-22T18:00'),
  endDate: dayjs('2023-03-22T14:33'),
};

export const sampleWithFullData: ITimeOffRequest = {
  id: 10834,
  startDate: dayjs('2023-03-23T05:21'),
  endDate: dayjs('2023-03-22T22:39'),
  status: 'transmit Frozen indexing',
};

export const sampleWithNewData: NewTimeOffRequest = {
  startDate: dayjs('2023-03-22T21:57'),
  endDate: dayjs('2023-03-23T12:06'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
