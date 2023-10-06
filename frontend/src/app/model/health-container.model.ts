import {HealthStatusDTO} from './health-status-dto.model';

export class HealthContainer {
  name!: string;
  isActive!: boolean;
  status?: HealthStatusDTO;
}