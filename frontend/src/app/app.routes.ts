import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';
import { BookingComponent } from './pages/booking/booking.component';
//import { BookingComponent } from './pages/booking/booking.component';

export const routes: Routes = [
    { path: '', component: HomeComponent, pathMatch: 'full', canActivate: [authGuard([])] },
    //{ path: '/', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'home', component: HomeComponent, canActivate: [authGuard([])] },
    { path: 'admin', component: HomeComponent, canActivate: [authGuard(['ADMIN'])] },
    { path: 'rent/:id', component: BookingComponent, canActivate: [authGuard([])] }
];
