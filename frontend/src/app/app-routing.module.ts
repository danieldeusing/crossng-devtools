import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

export const routes: Routes = [
  {path: '', redirectTo: 'status/app-health', pathMatch: 'full'},
  {
    path: 'status',
    data: {breadcrumb: 'Status', navCategory: 'Status'},
    children: [
      {
        path: '',
        redirectTo: 'app-health',
        pathMatch: 'full'
      },
      {
        path: 'app-health',
        loadChildren: () => import('./module/status/app-health/app-health.module').then(m => m.AppHealthModule),
        data: {breadcrumb: 'App Health'}
      },
      {
        path: 'ccr-activation',
        loadChildren: () => import('./module/status/ccr-activation/ccr-activation.module').then(
          m => m.CcrActivationModule),
        data: {breadcrumb: 'CCR Activation'}
      },
    ]
  },
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
