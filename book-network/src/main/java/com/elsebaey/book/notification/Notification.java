package com.elsebaey.book.notification;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {

    private NotificationStatus status;
    private String message;
    private String bookTitle;
}
