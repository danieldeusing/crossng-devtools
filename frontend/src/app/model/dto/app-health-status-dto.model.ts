export class AppHealthStatusDTO {
  responseCode!: number;
  status!: string;
  lastUpdated?: Date;
  errorMessage?: string;
}