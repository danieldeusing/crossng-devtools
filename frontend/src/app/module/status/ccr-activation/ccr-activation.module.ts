import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {CcrActivationRoutingModule} from './ccr-activation-routing.module';
import {CcrActivationComponent} from './ccr-activation.component';
import {MatTableModule} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';


@NgModule({
  declarations: [
    CcrActivationComponent
  ],
  imports: [
    CommonModule,
    CcrActivationRoutingModule,
    MatTableModule,
    MatIconModule,
    MatCardModule
  ]
})
export class CcrActivationModule {
}
