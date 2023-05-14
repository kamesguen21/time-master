import dayjs from 'dayjs/esm';

export interface ITimeOffRequest {
  id: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: string | null;
  userId?: number | null;
}

export type NewTimeOffRequest = Omit<ITimeOffRequest, 'id'> & { id: null };
