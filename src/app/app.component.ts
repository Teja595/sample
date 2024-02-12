import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
