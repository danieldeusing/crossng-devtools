import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppHealthComponent} from './app-health.component';

const routes: Routes = [
  { path: '', component: AppHealthComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppHealthRoutingModule {
}
