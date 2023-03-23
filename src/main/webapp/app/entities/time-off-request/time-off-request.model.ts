import dayjs from 'dayjs/esm';
import { IUserConfig } from 'app/entities/user-config/user-config.model';

export interface ITimeOffRequest {
  id: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: string | null;
  user?: Pick<IUserConfig, 'id'> | null;
}

export type NewTimeOffRequest = Omit<ITimeOffRequest, 'id'> & { id: null };
