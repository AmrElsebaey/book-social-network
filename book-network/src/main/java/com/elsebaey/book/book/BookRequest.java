package com.elsebaey.book.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        Integer id,

        @NotBlank(message = "100")
        @NotNull(message = "100")
        String title,

        @NotBlank(message = "101")
        @NotNull(message = "101")
        String author,

        @NotBlank(message = "102")
        @NotNull(message = "102")
        String isbn,

        @NotBlank(message = "103")
        @NotNull(message = "103")
        String synopsis,

        boolean shareable
) {
}
