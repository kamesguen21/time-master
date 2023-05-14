import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';
import { IWorkLog } from '../work-log/work-log.model';

export interface ITicket {
  id: number;
  jiraKey?: string | null;
  summary?: string | null;
  description?: string | null;
  userId?: number | null;
  status?: TicketStatus | null | string;
  workLogs?: IWorkLog[] | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
