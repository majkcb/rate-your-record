import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { CategoryComponent } from './features/category/category.component';
import { ReleaseDetailComponent } from './features/release/release-detail.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'category/:category', component: CategoryComponent },
    { path: 'release/:releaseId', component: ReleaseDetailComponent }
];
