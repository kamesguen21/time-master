import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  key: 'Sleek Centralized Fish',
  summary: 'Rubber deposit',
};

export const sampleWithPartialData: ITicket = {
  id: 76263,
  key: 'AGP Books Outdoors',
  summary: 'Cambridgeshire Brand Optional',
};

export const sampleWithFullData: ITicket = {
  id: 10402,
  key: 'programming',
  summary: 'cutting-edge',
  description: 'Plastic',
};

export const sampleWithNewData: NewTicket = {
  key: 'infrastructures',
  summary: 'Personal Fantastic',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
