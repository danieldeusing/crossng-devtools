import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {API_CONFIG} from '../../../environment/config';
import {CasLoginResponseDTO} from '../../../model/dto/cas-login-response.model';

@Injectable({
  providedIn: 'root'
})
export class CcrActivationService {
  private gsid = new BehaviorSubject<string>('');

  private backendHeaders = new HttpHeaders({
    'X-API-KEY': API_CONFIG.SECURITY_X_API_KEY,
    'Content-Type': 'text/plain'
  });

  constructor(private http: HttpClient) {
  }

  getGsid(): Observable<CasLoginResponseDTO> {
    return this.http.get<CasLoginResponseDTO>(`${this.getSanitizedBaseUrl()}/casLogin`, {headers: this.backendHeaders})
      .pipe(tap(casLoginResponse => {
          this.gsid.next(casLoginResponse.token);
        })
      );
  }

  getAppNames(): Observable<string[]> {
    return this.http.get<string[]>(
      `/crossng-systemmanagement/internal/api/apps/ccrAppIdsForOverview`, {
        headers: new HttpHeaders({
          'c3-gsid': this.gsid.value
        }),
        withCredentials: true
      });
  }

  getAppStatus(appName: string): Observable<any> {
    return this.http.get<any>(
      `/crossng-systemmanagement/internal/api/apps/apponlineactivationstatus/${appName}`, {
        headers: new HttpHeaders({
          'c3-gsid': this.gsid.value
        }),
        withCredentials: true
      });
  }

  private getSanitizedBaseUrl(): string {
    return API_CONFIG.BACKEND_URL.endsWith('/') ? API_CONFIG.BACKEND_URL.slice(0, -1) : API_CONFIG.BACKEND_URL;
  }
}
