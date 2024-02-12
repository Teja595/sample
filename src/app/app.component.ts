import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgForm } from '@angular/forms';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend';
  data: any; // This will store the fetched data
  loading: boolean = false;

  constructor(private http:HttpClient) {}

  ngOnInit() {
    this.getData();
  }
  user = {
    name: '',
    username: '',
    email: '',
    street: '',
    suite: '',
    city: '',
    zipcode: '',
    geoLat: null,
    geoLng: null,
    phone: '',
    website: '',
    companyName: '',
    companyCatchPhrase: '',
    companyBs: ''
  };

  onSubmit(form: NgForm) {
    this.http.post('http://localhost:8080/users', form.value)
      .subscribe({
        next: (response) => console.log('Success!', response),
        error: (error) => console.error('Error!', error)
      });
  }
  getData(): void {
    this.loading = true;
    const url = 'https://jsonplaceholder.typicode.com/users'; // Replace with your target URL
    this.http.get(url)
      .subscribe({
        next: (response) => {
          this.data = response; // Store the data received from the API
          console.log(this.data); // Optional: Log the data to the console
          this.loading = false;
        },
        error: (error) => {
          console.error('There was an error!', error);
          this.loading = false;
        }
      });
    }



}
