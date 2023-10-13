import {Component, OnDestroy, OnInit} from '@angular/core';
import {interval, startWith, Subject, takeUntil} from 'rxjs';
import {mergeMap, switchMap} from 'rxjs/operators';
import {CcrActivationService} from './ccr-activation.service';
import {CcrActivationStatus} from '../../../model/ccr-activation-status.model';

@Component({
  selector: 'app-ccr-activation',
  templateUrl: './ccr-activation.component.html',
  styleUrls: ['./ccr-activation.component.scss']
})
export class CcrActivationComponent implements OnInit, OnDestroy {
  appStatuses: CcrActivationStatus[] = [];
  loginError: boolean = false;

  private destroy$ = new Subject<void>();

  constructor(private service: CcrActivationService) {
  }

  ngOnInit() {
    this.service.getGsid().subscribe(() => {
      this.service
        .getAppNames()
        .pipe(
          switchMap(appNames =>
            interval(60000).pipe(
              startWith(0),
              switchMap(() => appNames),
              mergeMap(appName => this.service.getAppStatus(appName))
            )
          ),
          takeUntil(this.destroy$)
        )
        .subscribe({
          next: (statusArray: CcrActivationStatus[]) => {
          },
          error: () => {
            this.loginError = true;
          },
        });
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
