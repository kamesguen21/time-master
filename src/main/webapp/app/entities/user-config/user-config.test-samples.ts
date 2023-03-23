import { IUserConfig, NewUserConfig } from './user-config.model';

export const sampleWithRequiredData: IUserConfig = {
  id: 72604,
  login: 'Auto',
  firstName: 'Wendell',
  lastName: 'Pouros',
  email: 'Jedediah.McLaughlin@hotmail.com',
  phoneNumber: 'Grocery Human',
};

export const sampleWithPartialData: IUserConfig = {
  id: 61761,
  login: 'Shoes Rubber Mouse',
  firstName: 'Layla',
  lastName: 'Davis',
  email: 'Hellen_Waters@gmail.com',
  phoneNumber: 'Corners Maryland',
};

export const sampleWithFullData: IUserConfig = {
  id: 43743,
  login: 'Coordinator',
  firstName: 'William',
  lastName: 'Bins',
  email: 'Jacquelyn7@yahoo.com',
  phoneNumber: 'Bedfordshire Indonesia',
};

export const sampleWithNewData: NewUserConfig = {
  login: "magenta indexing People's",
  firstName: 'Garret',
  lastName: 'Frami',
  email: 'Domenica64@yahoo.com',
  phoneNumber: 'transmitting View Pants',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
