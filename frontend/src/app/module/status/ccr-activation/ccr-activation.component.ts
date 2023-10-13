import {Component, OnInit} from '@angular/core';
import {interval, startWith} from 'rxjs';
import {mergeMap, switchMap} from 'rxjs/operators';
import {CcrActivationService} from './ccr-activation.service';
import {CcrActivationStatus} from '../../../model/ccr-activation-status.model';

@Component({
  selector: 'app-ccr-activation',
  templateUrl: './ccr-activation.component.html',
  styleUrls: ['./ccr-activation.component.scss']
})
export class CcrActivationComponent implements OnInit {
  appStatuses: CcrActivationStatus[] = [];
  loginError: boolean = false;

  private appNameToStatusMap: { [key: string]: CcrActivationStatus } = {};

  constructor(private service: CcrActivationService) {
  }

  ngOnInit() {
    this.service.getGsid().subscribe(() => {
      this.service.getAppNames().pipe(
        switchMap(appNames => interval(60000).pipe(
          startWith(0),
          switchMap(() => appNames),
          mergeMap(appName => this.service.getAppStatus(appName))
        ))
      ).subscribe({
        next: (statusArray: CcrActivationStatus[]) => {
          for (let status of statusArray) {
            let appName = status.applicationId;
            this.appNameToStatusMap[appName] = status;
          }
          this.appStatuses = Object.values(this.appNameToStatusMap);
        },
        error: () => {
          this.loginError = true;
        }
      });
    });
  }
}
