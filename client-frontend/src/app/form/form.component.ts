import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormService } from './form.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Field } from '../model/field';
import { PaymentMethod } from '../model/paymentmethod';
import { MatStepper } from '@angular/material/stepper';
import { ValueTransformer } from '@angular/compiler/src/util';
import { TokenStorageService } from '../authentication/token-storage.service';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class PaymentMethodsComponent implements OnInit {

  constructor(private formService: FormService,
              private tokenStorageService: TokenStorageService,
              private router: Router) { }


  paymentMethods: PaymentMethod[] = [];
  fields: any = {};

  forms: any = {};
  subscriptionForm: FormGroup;
  paymentMethodsWithSubscription: PaymentMethod[];

  ngOnInit() {
    this.createForms();

    // create the form for subscription
    this.subscriptionForm = new FormGroup(
      {
        paymentFrequency: new FormControl('', [Validators.required]),
        cyclesNumber: new FormControl('', [Validators.required, Validators.min(1)])
      }
    );

  }

  createForms() {
    // get all available payment methods
    this.formService.getPaymentMethods().subscribe(
      data => {
        for (let pm of data) {
          this.paymentMethods.push(pm);
        }

        // create a form for every payment method
        for (let pm of this.paymentMethods) {
          this.getFields(pm.name.toLowerCase());
        }

      },
      error => {
        console.log('An error ocurred.');
      }
    );
  }

  getFields(paymentMethodName: string) {

    let list: Field[] = [];
    let form: FormGroup;

    // get fields for a payment method
    this.formService.getFields(paymentMethodName).subscribe(
      data => {
        for (let field of data) {
          list.push(field);
        }

        this.fields[paymentMethodName] = list;

        // create a form group from the list of fields
        form = this.createFormGroup(list);
        this.forms[paymentMethodName] = form;
      },
      error => {
        console.log('An error ocurred. Please try again!');
      }
    );
  }

  // create a form group from the list of fields
  createFormGroup(fields: Field[]) {
    const group: any = {};

    fields.forEach(field => {
      group[field.name] = field.required ? new FormControl('', Validators.required) : new FormControl('');
    });

    return new FormGroup(group);
  }

  submitForm(stepper: MatStepper, paymentMethod: string, i: number) {

    let form = this.forms[paymentMethod.toLowerCase()];

    const valuesList: any = {};

    // create the JSON object for the body
    Object.keys(form.controls).forEach(key => {
      valuesList[key] = form.value[key];
    });


    this.formService.addPaymentMethod(paymentMethod.toLowerCase(), valuesList).subscribe(
      data => {
        alert('You have successfully added ' + paymentMethod + '!');

        stepper.next();
      },
      error => {
        alert('An error ocurred. Please try again!');
      }
    );

  }

  skipForm(stepper: MatStepper, i: number) {
    if (i < (this.paymentMethods.length)) {
      stepper.next();
    } else {
      alert('You have successfully finished the registration process!');

      this.getLink();
    }
  }

  submitSubscriptionForm(continueParameter: boolean) {

    const valuesList: any = {
      frequency: this.subscriptionForm.value.paymentFrequency,
      cyclesNumber: this.subscriptionForm.value.cyclesNumber
    };

    this.formService.addSubscriptionPlan(valuesList).subscribe(
      data => {
        // clear form or redirect to home page
        if (continueParameter) {
          alert('You have successfully created a subscription plan!');
          this.subscriptionForm.reset();
        } else {
          alert('You have successfully finished the registration process!');

          this.getLink();
        }
      },
      error => {
        alert('An error ocurred. Please try again!');
      }
    );
  }

  getLink() {
    let link: string;
    this.formService.getLink().subscribe(
      data => {
        link = data.url;

        this.tokenStorageService.signOut();

        document.location.href = link;
      },
      error => {
        link = 'https://localhost:4200/#/';

        this.tokenStorageService.signOut();

        document.location.href = link;
      }
    );

  }

}
