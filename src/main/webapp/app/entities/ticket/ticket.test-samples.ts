import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  jiraKey: 'Sleek Centralized Fish',
  summary: 'Rubber deposit',
};

export const sampleWithPartialData: ITicket = {
  id: 87939,
  jiraKey: 'Lek',
  summary: 'Outdoors',
  userId: 76849,
  status: TicketStatus['TO_REVIEW'],
};

export const sampleWithFullData: ITicket = {
  id: 84212,
  jiraKey: 'Brand Optional',
  summary: 'Fresh',
  description: 'Pants card Guam',
  userId: 48181,
  status: TicketStatus['PENDING'],
};

export const sampleWithNewData: NewTicket = {
  jiraKey: 'Concrete Personal',
  summary: 'seize',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
