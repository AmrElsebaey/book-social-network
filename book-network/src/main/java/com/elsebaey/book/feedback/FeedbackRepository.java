package com.elsebaey.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

   @Query("SELECT feedback" +
           " FROM Feedback feedback " +
           "WHERE feedback.book.id = :bookId")
    Page<Feedback> findAllByBookId(Integer bookId, Pageable pageable);
}
