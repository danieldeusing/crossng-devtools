import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {AppHealthRoutingModule} from './app-health-routing.module';
import {AppHealthComponent} from './app-health.component';
import {MatIconModule} from '@angular/material/icon';
import {MatTableModule} from '@angular/material/table';
import {MatButtonModule} from '@angular/material/button';
import {AddAppDialogComponent} from './add-app-dialog/add-app-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';


@NgModule({
  declarations: [
    AppHealthComponent,
    AddAppDialogComponent
  ],
  imports: [
    CommonModule,
    AppHealthRoutingModule,
    MatIconModule,
    MatTableModule,
    MatButtonModule,
    MatDialogModule,
    MatRadioModule,
    MatSelectModule,
    FormsModule,
    MatInputModule
  ]
})
export class AppHealthModule {
}
