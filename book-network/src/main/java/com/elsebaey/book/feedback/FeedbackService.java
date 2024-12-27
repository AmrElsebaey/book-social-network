package com.elsebaey.book.feedback;

import com.elsebaey.book.book.Book;
import com.elsebaey.book.book.BookRepository;
import com.elsebaey.book.common.PageResponse;
import com.elsebaey.book.exception.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + request.bookId()));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("You cannot give feedback for this book");
        }
//        User user = (User) connectedUser.getPrincipal();
        if (connectedUser.getName().equals(book.getCreatedBy())) {
            throw new OperationNotPermittedException("You cannot give feedback for your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return repository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
//        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = repository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, connectedUser.getName()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
