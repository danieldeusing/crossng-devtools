import {AppHealthStatusDTO} from './dto/app-health-status-dto.model';

export class AppHealth {
  name!: string;
  isActive!: boolean;
  status?: AppHealthStatusDTO;
}