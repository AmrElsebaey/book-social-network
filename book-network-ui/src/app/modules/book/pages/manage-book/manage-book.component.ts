import { Component, OnInit } from '@angular/core';
import { BookRequest } from '../../../../services/models';
import { BookService } from '../../../../services/services';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrl: './manage-book.component.scss',
})
export class ManageBookComponent implements OnInit {
  errorMsg: Array<string> = [];
  bookRequest: BookRequest = {
    author: '',
    isbn: '',
    synopsis: '',
    title: '',
  };
  selectedBookCover: any;
  selectedPicture: string | undefined;

  constructor(
    private bookService: BookService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private toastService: ToastrService
  ) {}

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    if (bookId) {
      this.bookService
        .findBookById({
          'book-id': bookId,
        })
        .subscribe({
          next: (book) => {
            this.bookRequest = {
              id: book.id,
              title: book.title as string,
              author: book.author as string,
              isbn: book.isbn as string,
              synopsis: book.synopsis as string,
              shareable: book.shareable,
            };
            this.selectedPicture = 'data:image/jpg;base64,' + book.cover;
          },
        });
    }
  }

  saveBook() {
    this.bookService
      .saveBook({
        body: this.bookRequest,
      })
      .subscribe({
        next: (bookId) => {
          const base64Image = this.selectedBookCover;

          this.bookService
            .uploadBookCoverPicture({
              'book-id': bookId,
              body: { file: base64Image },
            })
            .subscribe({
              next: () => {
                this.toastService.success('Book saved successfully', 'Done!');
                this.router.navigate(['/books/my-books']);
              },
              error: (err) => {
                this.toastService.error('Something went wrong', 'Oups!!');},
            });
        },
        error: (err) => {
          console.error('Error saving book:', err);
        },
      });
  }

  onFileSelected(event: any) {
    this.selectedBookCover = event.target.files[0];
    console.log(this.selectedBookCover);

    if (this.selectedBookCover) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }
}
