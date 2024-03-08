import { Component, OnInit } from '@angular/core';
import { User } from './user';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit{
  title = 'Device ID data';
  
  users: User[] = []; // Initialize users array to empty
  currentPage: number = 0;
  rowsPerPage: number = 10;
  totalPages: number = 0;
  message: string = '';
  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchData(this.currentPage, this.rowsPerPage);
  }

  fetchData(page: number, rows: number): void {
    const url = `http://learn2code.redgrape.tech:8081/x?page=${page}&rows=${rows}`;
    this.http.get<any>(url)
      .subscribe(response => {
        // Check if the response includes 'content' and 'totalPages'
        if (response && response.content && typeof response.totalPages === 'number') {
          // Map through each item in the 'content' array and add 'isActive' property set to true
          this.users = response.content.map((user: User) => ({
            ...user,
            isActive: true // Add 'isActive' property
          }));
          this.totalPages = response.totalPages;
          this.message = '';
        } else {
          // Handle unexpected response structure
          console.error('Unexpected response structure:', response);
          this.message = 'Failed to load data due to unexpected response structure.';
        }
      }, error => {
        console.error('There was an error!', error);
        this.message = 'An error occurred while fetching data.';
      });
  }
  
  

 onNextClick(): void {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
    this.fetchData(this.currentPage, this.rowsPerPage);
  } else {
    // Set a message when there are no more pages
    this.message = 'No more rows.';
  }
}
onPrevClick(): void {
  if (this.currentPage >= 0) { // Ensure we don't go below the first page
    this.currentPage--;
    this.fetchData(this.currentPage, this.rowsPerPage);
  }
}


}
