package com.elsebaey.book.feedback;

import com.elsebaey.book.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .comment(request.comment())
                .rating(request.rating())
                .book(Book.builder()
                        .id(request.bookId())
                        .build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, String userId) {
        return FeedbackResponse.builder()
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .ownFeedback(feedback.getCreatedBy().equals(userId))
                .build();
    }
}
