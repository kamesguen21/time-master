import dayjs from 'dayjs/esm';
import { TimeOffRequestStatus } from 'app/entities/enumerations/time-off-request-status.model';

export interface ITimeOffRequest {
  id: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: TimeOffRequestStatus | null;
  userId?: number | null;
}

export type NewTimeOffRequest = Omit<ITimeOffRequest, 'id'> & { id: null };
