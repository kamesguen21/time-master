import dayjs from 'dayjs/esm';

import { IWorkLog, NewWorkLog } from './work-log.model';

export const sampleWithRequiredData: IWorkLog = {
  id: 54986,
  timeSpent: 713,
  date: dayjs('2023-05-14T08:56'),
};

export const sampleWithPartialData: IWorkLog = {
  id: 5973,
  timeSpent: 79521,
  date: dayjs('2023-05-14T06:58'),
};

export const sampleWithFullData: IWorkLog = {
  id: 18688,
  timeSpent: 98139,
  date: dayjs('2023-05-13T17:20'),
  userId: 90960,
};

export const sampleWithNewData: NewWorkLog = {
  timeSpent: 38741,
  date: dayjs('2023-05-13T18:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
