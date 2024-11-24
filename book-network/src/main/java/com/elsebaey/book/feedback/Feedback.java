package com.elsebaey.book.feedback;

import com.elsebaey.book.book.Book;
import com.elsebaey.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback extends BaseEntity {

    private Double rating;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


}
