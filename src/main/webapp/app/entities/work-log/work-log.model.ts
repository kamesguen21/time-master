import dayjs from 'dayjs/esm';
import { IUserConfig } from 'app/entities/user-config/user-config.model';
import { ITicket } from 'app/entities/ticket/ticket.model';

export interface IWorkLog {
  id: number;
  timeSpent?: number | null;
  date?: dayjs.Dayjs | null;
  user?: Pick<IUserConfig, 'id'> | null;
  ticket?: Pick<ITicket, 'id'> | null;
}

export type NewWorkLog = Omit<IWorkLog, 'id'> & { id: null };
