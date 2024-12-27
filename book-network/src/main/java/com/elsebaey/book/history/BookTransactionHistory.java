package com.elsebaey.book.history;

import com.elsebaey.book.book.Book;
import com.elsebaey.book.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class BookTransactionHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "user_id")
    private String userId;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    private boolean returned;
    private boolean returnApproved;

}
