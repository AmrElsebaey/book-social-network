package com.elsebaey.book.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(String ownerId) {
        return (root, query, cb) ->
                cb.equal(root.get("createdBy"), ownerId);
    }
}
