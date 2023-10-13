import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {AppHealthService} from './app-health.service';
import {AppHealth} from '../../../model/app-health.model';
import {interval, Subject, Subscription, takeUntil} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../component/common/confirm-dialog/confirm-dialog.component';
import {AddAppDialogComponent} from './add-app-dialog/add-app-dialog.component';
import {API_CONFIG} from '../../../environment/config';

@Component({
  selector: 'app-status-app-health',
  templateUrl: './app-health.component.html',
  styleUrls: ['./app-health.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppHealthComponent implements OnInit, OnDestroy {

  containers: AppHealth[] = [];
  displayedContainers: AppHealth[] = [];
  displayedColumns: string[] = ['name', 'activeState', 'timestamp', 'status', 'errorCode', 'delete'];
  hideInactive: boolean = true;
  refreshInterval = Number(API_CONFIG.REFRESH_INTERVAL);

  private healthCheckSubscriptions: Subscription[] = [];
  private destroyed$ = new Subject<void>();

  constructor(public healthCheckService: AppHealthService,
              private dialog: MatDialog,
              private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.refreshContainers();
    this.updateHealthStates();
    this.initiateHealthChecks();
  }

  ngOnDestroy(): void {
    this.healthCheckSubscriptions.forEach(sub => sub.unsubscribe());
    this.destroyed$.next();
    this.destroyed$.complete();
  }


  updateHealthStates(): void {
    this.containers.forEach(container => {
      if (container.isActive) {
        this.healthCheckService.checkHealth(container.name)
          .pipe(takeUntil(this.destroyed$))
          .subscribe(status => {
            container.status = status;
            this.cdr.detectChanges();
          });
      }
    });
  }

  addContainer(): void {
    const inactiveContainers = this.containers.filter(container => !container.isActive);
    const dialogRef = this.dialog.open(AddAppDialogComponent, {
      width: '350px',
      data: {inactiveContainers: inactiveContainers}
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(result => {
        if (result) {
          this.healthCheckService.addContainer(result);
          this.containers = this.healthCheckService.getContainers();
          this.refreshContainers();
          this.initiateHealthChecks();
        }
      });
  }

  removeContainer(containername: string): void {
    this.healthCheckService.removeContainer(containername);
    this.refreshContainers();
    this.initiateHealthChecks()
  }

  toggleContainerActive(containername: string): void {
    this.healthCheckService.toggleContainerActive(containername);
    this.refreshContainers();
    this.initiateHealthChecks()
  }

  openResetDialog(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '450px',
      data: {
        title: 'Factory Reset',
        text: 'Do you really want to factory reset your settings?'
      }
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(result => {
        if (result === 'yes') {
          this.healthCheckService.factoryReset();
          this.containers = this.healthCheckService.getContainers();
        }
      });
  }

  toggleInactiveContainersVisibility(): void {
    this.hideInactive = !this.hideInactive;
    this.refreshContainers();
  }

  refreshContainers(): void {
    this.containers = this.healthCheckService.getContainers();
    if (this.hideInactive) {
      this.displayedContainers = this.sortContainers(this.containers.filter(container => container.isActive));
    } else {
      this.displayedContainers = this.sortContainers([...this.containers]);
    }
    this.cdr.detectChanges();
  }

  getTimeDifferenceInSeconds(lastUpdated: Date): number {
    if (!lastUpdated) {
      return 0;
    }

    const currentTime = new Date();
    const timeDifference = currentTime.getTime() - new Date(lastUpdated).getTime();

    return Math.floor(timeDifference / 1000);
  }

  private sortContainers(containers: AppHealth[]): AppHealth[] {
    return containers.sort((a, b) => {
      if (a.isActive === b.isActive) {
        return a.name.localeCompare(b.name);
      }
      return a.isActive ? -1 : 1;
    });
  }

  private initiateHealthChecks(): void {
    this.healthCheckSubscriptions.forEach(sub => sub.unsubscribe());
    this.healthCheckSubscriptions = [];

    this.containers.forEach((container, index) => {
      if (container.isActive) {
        const subscription = interval(this.refreshInterval + index * 500)
          .pipe(takeUntil(this.destroyed$))
          .subscribe(
            data => {
              this.healthCheckService.checkHealth(container.name)
                .pipe(takeUntil(this.destroyed$))
                .subscribe(status => {
                  const targetContainer = this.containers.find(c => c.name === container.name);
                  if (targetContainer) {
                    targetContainer.status = status;
                    targetContainer.status.lastUpdated = new Date();
                    this.cdr.detectChanges();
                  }
                });
            });
        this.healthCheckSubscriptions.push(subscription);
      }
    });
  }

}
