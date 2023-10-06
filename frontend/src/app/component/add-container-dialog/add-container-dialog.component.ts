import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {HealthContainer} from '../../model/health-container.model';

@Component({
  selector: 'app-add-container-dialog',
  templateUrl: './add-container-dialog.component.html',
  styleUrls: ['./add-container-dialog.component.scss']
})
export class AddContainerDialogComponent {

  selectedOption: 'dropdown' | 'input' = 'dropdown';
  inactiveContainers: HealthContainer[];
  selectedContainer: string = "";
  inputContainerName: string = "";

  constructor(public dialogRef: MatDialogRef<AddContainerDialogComponent>,
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
