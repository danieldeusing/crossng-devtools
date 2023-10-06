export class HealthStatusDTO {
  responseCode!: number;
  status!: string;
  lastUpdated?: Date;
  errorMessage?: string;
}