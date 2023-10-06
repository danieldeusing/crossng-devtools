import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, of, tap} from 'rxjs';
import {HealthStatusDTO} from '../model/health-status-dto.model';
import {API_CONFIG} from '../environment/config';
import {HealthContainer} from '../model/health-container.model';

@Injectable({
  providedIn: 'root'
})
export class HealthStatusService {

  private STORAGE_KEY = 'containers_state';

  private headers = new HttpHeaders({
    'X-API-KEY': API_CONFIG.SECURITY_X_API_KEY
  });

  // Default containers with initial active state
  private defaultContainerStates: HealthContainer[] = [
    {name: "crossng-ais", isActive: true},
    {name: "crossng-common-magellan", isActive: true},
    {name: "crossng-communicator", isActive: true},
    {name: "crossng-configuration", isActive: true},
    {name: "crossng-outputmanager", isActive: true},
    {name: "ppm", isActive: true},
    {name: "crossng-preparer", isActive: true},
    {name: "crossng-printing-app", isActive: true},
    {name: "crossng-sales-backoffice", isActive: true},
    {name: "crossng-sales-columbus", isActive: true},
    {name: "crossng-sales-frontoffice", isActive: true},
    {name: "crossng-scheduler", isActive: true},
    {name: "crossng-systemconfig", isActive: true},
    {name: "crossng-systemmanagement", isActive: true},
    {name: "crossng-update-app", isActive: true},
    {name: "crossng-datahub", isActive: false},
    {name: "crossng-dashboard", isActive: false},
    {name: "crossng-service", isActive: false},
    {name: "crossng-parts", isActive: false},
    {name: "accounting", isActive: false},
    {name: "cdi", isActive: false},
    {name: "libra", isActive: false},
    {name: "cashdesk", isActive: false},
    {name: "timemachine", isActive: false},
    {name: "promotionaloffer", isActive: false},
    {name: "physical-vehicle", isActive: false},
    {name: "evaluations", isActive: false},
    {name: "vehiclesalesfolder", isActive: false},
    {name: "crossng-packages", isActive: false},
    {name: "tirestorage", isActive: false},
    {name: "service-base", isActive: false},
    {name: "crossng-parts-base", isActive: false},
    {name: "crossng-partscatalogue", isActive: false},
    {name: "asa-manager", isActive: false},
    {name: "vatregister", isActive: false},
    {name: "crossng-soext", isActive: false},
    {name: "rpa", isActive: false},
    {name: "crossng-lead-management", isActive: false},
    {name: "free-invoice", isActive: false},
    {name: "incoming-invoice", isActive: false},
    {name: "commission", isActive: false},
    {name: "clocking", isActive: false},
    {name: "infocenter", isActive: false},
    {name: "crossng-tasks", isActive: false},
    {name: "crossng-service-pricingengine", isActive: false}
  ];

  constructor(private http: HttpClient) {
    if (!localStorage.getItem(this.STORAGE_KEY)) {
      this.setContainers(this.defaultContainerStates);
    }
  }

  checkHealth(appName: string): Observable<HealthStatusDTO> {
    return this.http.get<HealthStatusDTO>(`${API_CONFIG.BACKEND_URL}check-health/${appName}`, {headers: this.headers})
      .pipe(
        tap((status: HealthStatusDTO) => {
          const containers = this.getContainers();
          const container = containers.find(c => c.name === appName);
          if (container) {
            container.status = status;
            container.status.lastUpdated = new Date();
            this.setContainers(containers);
          }
        }),
        catchError(error => {
          const containers = this.getContainers();
          const container = containers.find(c => c.name === appName);
          const currentTime = new Date();
          const defaultErrorResponse: HealthStatusDTO = {
            responseCode: error.status,
            status: 'DOWN',
            errorMessage: error?.error?.errorMessage ? `${error.error.errorMessage}`
              : `${error.status} ${error.statusText}`,
            lastUpdated: currentTime
          };
          if (container) {
            container.status = defaultErrorResponse;
          }
          this.setContainers(containers);
          return of(defaultErrorResponse);
        })
      );
  }


  getContainers(): HealthContainer[] {
    const containers = localStorage.getItem(this.STORAGE_KEY);
    return containers ? JSON.parse(containers) : [];
  }

  setContainers(containers: HealthContainer[]): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(containers));
  }

  toggleContainerActive(containerName: string): void {
    const containers = this.getContainers();
    const container = containers.find(c => c.name === containerName);
    if (container) {
      container.isActive = !container.isActive;
      this.setContainers(containers);
    }
  }

  addContainer(containerName: string): void {
    const containers = this.getContainers();
    containers.push({name: containerName, isActive: true});
    this.setContainers(containers);
  }

  removeContainer(containerName: string): void {
    const containers = this.getContainers();
    const updatedContainers = containers.filter(c => c.name !== containerName);
    this.setContainers(updatedContainers);
  }

  isContainerActive(containerName: string): boolean {
    const container = this.getContainers().find(c => c.name === containerName);
    return container ? container.isActive : false;
  }

  factoryReset(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.setContainers(this.defaultContainerStates);
  }
}
