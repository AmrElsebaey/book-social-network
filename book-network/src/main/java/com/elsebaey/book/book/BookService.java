package com.elsebaey.book.book;

import com.elsebaey.book.common.PageResponse;
import com.elsebaey.book.exception.OperationNotPermittedException;
import com.elsebaey.book.file.FileStorageService;
import com.elsebaey.book.history.BookTransactionHistory;
import com.elsebaey.book.history.BookTransactionHistoryRepository;
import com.elsebaey.book.notification.Notification;
import com.elsebaey.book.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.elsebaey.book.book.BookSpecification.withOwnerId;
import static com.elsebaey.book.notification.NotificationStatus.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public Integer save(BookRequest request, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
//        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));

    }

    public PageResponse<BookResponse> findAllBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getName());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(connectedUser.getName()), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, connectedUser.getName());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(Integer page, Integer size, Authentication connectedUser) {
//        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, connectedUser.getName());

        List<BorrowedBookResponse> bookResponses = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        if (!connectedUser.getName().equals(book.getCreatedBy())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        book.setShareable(!book.isShareable());
        return bookRepository.save(book).getId();
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(connectedUser.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        book.setArchived(!book.isArchived());
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (connectedUser.getName().equals(book.getCreatedBy())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository
                .isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory =
                BookTransactionHistory.builder()
                        .book(book)
                        .userId(connectedUser.getName())
                        .returnApproved(false)
                        .returned(false)
                        .build();
        notificationService.sendNotification(
                book.getCreatedBy(),
                Notification.builder()
                        .status(BORROWED)
                        .message("Your book has been borrowed")
                        .bookTitle(book.getTitle())
                        .build());
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (connectedUser.getName().equals(book.getCreatedBy())) {
            throw new OperationNotPermittedException("You cannot return your own book");
        }
        BookTransactionHistory history = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrow this book"));
        history.setReturned(true);
        var saved = bookTransactionHistoryRepository.save(history);
        notificationService.sendNotification(
                book.getCreatedBy(),
                Notification.builder()
                        .status(RETURNED)
                        .message("Your book has been returned")
                        .bookTitle(book.getTitle())
                        .build()
        );
        return saved.getId();

    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (!connectedUser.getName().equals(book.getCreatedBy())) {
            throw new OperationNotPermittedException("You cannot return your own book");
        }
        BookTransactionHistory history = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. you cannot approve its return"));
        history.setReturnApproved(true);
        var saved = bookTransactionHistoryRepository.save(history);
        notificationService.sendNotification(
                history.getCreatedBy(),
                Notification.builder()
                        .status(RETURN_APPROVED)
                        .message("Your book return has been approved")
                        .bookTitle(book.getTitle())
                        .build()
        );
        return saved.getId();
    }

    public void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));
//        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file, connectedUser.getName());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
