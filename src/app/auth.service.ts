// import { Injectable } from '@angular/core';
// import { Router } from '@angular/router';

// @Injectable({
//   providedIn: 'root'
// })
// export class AuthService {

//   private loggedIn = false;

//   constructor(private router: Router) {}

//   login(username: string, password: string): boolean {
//     if (username === 'abc' && password === '123') {
//       this.loggedIn = true;
//       localStorage.setItem('isLoggedIn', 'true');
//       this.router.navigate(['/']);
//       return true;
//     }
//     return false;
//   }

//   logout(): void {
//     this.loggedIn = false;
//     localStorage.removeItem('isLoggedIn');
//     this.router.navigate(['/login']);
//   }

//   isLoggedIn(): boolean {
//     return localStorage.getItem('isLoggedIn') === 'true';
//   }
// }
