import dayjs from 'dayjs/esm';

import { IWorkLog, NewWorkLog } from './work-log.model';

export const sampleWithRequiredData: IWorkLog = {
  id: 54986,
  timeSpent: 713,
  date: dayjs('2023-03-23T06:56'),
};

export const sampleWithPartialData: IWorkLog = {
  id: 2204,
  timeSpent: 5973,
  date: dayjs('2023-03-22T17:13'),
};

export const sampleWithFullData: IWorkLog = {
  id: 30554,
  timeSpent: 18688,
  date: dayjs('2023-03-22T12:45'),
};

export const sampleWithNewData: NewWorkLog = {
  timeSpent: 87336,
  date: dayjs('2023-03-22T14:29'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
