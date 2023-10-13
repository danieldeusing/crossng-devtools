export class CcrActivationStatus {
  id!: number;
  applicationId!: string;
  applicationVersion!: string;
  tenantGroupMatchCode!: string;
  isOnline!: boolean;
  runningOffline!: boolean;
  buildTimestamp!: string;
}