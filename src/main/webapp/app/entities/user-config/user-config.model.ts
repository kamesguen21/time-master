export interface IUserConfig {
  id: number;
  login?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
}

export type NewUserConfig = Omit<IUserConfig, 'id'> & { id: null };
