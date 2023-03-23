export interface ITicket {
  id: number;
  key?: string | null;
  summary?: string | null;
  description?: string | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
