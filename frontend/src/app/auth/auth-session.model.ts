export interface AuthSession {
  username: string;
  displayName: string;
  roles: string[];
  canViewPayroll: boolean;
}
