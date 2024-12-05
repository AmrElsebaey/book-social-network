import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BookRoutingModule } from './book-routing.module';
import { MainComponent } from './pages/main/main.component';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';
import { BorrowedBookListComponent } from './pages/borrowed-book-list/borrowed-book-list.component';
import { ReturnedBookComponent } from './pages/returned-book/returned-book.component';
import { MenuComponent } from './components/menu/menu.component';
import { BookCardComponent } from './components/book-card/book-card.component';
import { RatingComponent } from './components/rating/rating.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BookDetailsComponent } from './pages/book-details/book-details.component';

@NgModule({
  declarations: [
    MainComponent,
    BookListComponent,
    MyBooksComponent,
    ManageBookComponent,
    BorrowedBookListComponent,
    ReturnedBookComponent,
    BookCardComponent,
    RatingComponent,
    MenuComponent,
    BookDetailsComponent,
  ],
  imports: [CommonModule, BookRoutingModule, FormsModule, RouterModule],
})
export class BookModule {}
