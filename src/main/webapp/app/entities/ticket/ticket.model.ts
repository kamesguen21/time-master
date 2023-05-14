export interface ITicket {
  id: number;
  jiraKey?: string | null;
  summary?: string | null;
  description?: string | null;
  userId?: number | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
