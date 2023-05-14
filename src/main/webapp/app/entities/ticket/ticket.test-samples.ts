import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  jiraKey: 'Sleek Centralized Fish',
  summary: 'Rubber deposit',
};

export const sampleWithPartialData: ITicket = {
  id: 81176,
  jiraKey: 'national Electronics Chief',
  summary: 'connecting Towels',
  userId: 10402,
};

export const sampleWithFullData: ITicket = {
  id: 14064,
  jiraKey: 'Internal cutting-edge alliance',
  summary: 'Central infrastructures',
  description: 'Personal Fantastic',
  userId: 70014,
};

export const sampleWithNewData: NewTicket = {
  jiraKey: 'pink',
  summary: 'FTP applications',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
