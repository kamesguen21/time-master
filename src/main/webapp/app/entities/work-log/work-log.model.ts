import dayjs from 'dayjs/esm';
import { ITicket } from 'app/entities/ticket/ticket.model';

export interface IWorkLog {
  id: number;
  timeSpent?: number | null;
  date?: dayjs.Dayjs | null;
  userId?: number | null;
  userName?: string | null;
  ticket?: ITicket | null;
}

export type NewWorkLog = Omit<IWorkLog, 'id'> & { id: null };
