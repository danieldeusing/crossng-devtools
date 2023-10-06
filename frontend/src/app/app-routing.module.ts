import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HealthStatusComponent} from './component/health-status/health-status.component';

const routes: Routes = [
  { path: '', redirectTo: '/health-status', pathMatch: 'full' },
  { path: 'health-status', component: HealthStatusComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
