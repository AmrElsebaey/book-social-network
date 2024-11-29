package com.elsebaey.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory,Integer> {

    @Query(
            "select history from BookTransactionHistory history "+
            "where history.user.id = :userId"
            )
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query(
            "select history from BookTransactionHistory history "+
                    "where history.book.owner.id = :userId"
    )
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query(
            "SELECT COUNT(*) > 0" +
                    " FROM BookTransactionHistory history WHERE history.book.id = :bookId" +
                    " AND history.returnApproved = false "
    )
    boolean isAlreadyBorrowed(Integer bookId, Integer userId);

    @Query(
            "SELECT history "+
                    "FROM BookTransactionHistory history WHERE history.book.id = :bookId" +
                    " AND history.user.id= :userId "+
                    "AND history.returned = false "+
                    "AND history.returnApproved = false"
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("SELECT history "+
            "FROM BookTransactionHistory history WHERE history.book.id = :bookId" +
            " AND history.book.owner.id= :ownerId "+
            "AND history.returned = true "+
            "AND history.returnApproved = false")
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer ownerId);
}
