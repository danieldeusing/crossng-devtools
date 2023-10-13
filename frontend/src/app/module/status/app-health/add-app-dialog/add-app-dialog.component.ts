import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AppHealth} from '../../../../model/app-health.model';

@Component({
  selector: 'app-status-app-health-add-app-dialog',
  templateUrl: './add-app-dialog.component.html',
  styleUrls: ['./add-app-dialog.component.scss']
})
export class AddAppDialogComponent {

  selectedOption: 'dropdown' | 'input' = 'dropdown';
  inactiveContainers: AppHealth[];
  selectedContainer: string = "";
  inputContainerName: string = "";

  constructor(public dialogRef: MatDialogRef<AddAppDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.inactiveContainers = data.inactiveContainers;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    const result = this.selectedOption === 'dropdown' ? this.selectedContainer : this.inputContainerName;
    this.dialogRef.close(result);
  }
}
