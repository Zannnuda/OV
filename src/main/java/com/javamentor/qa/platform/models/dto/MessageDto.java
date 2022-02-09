package com.javamentor.qa.platform.models.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto  implements Serializable {

    private Long id;
    private String message;
    private LocalDateTime lastRedactionDate;
    private LocalDateTime persistDate;
    private Long userSenderId;
    private Long chatId;
    private String imageLink;

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", message='" + message +
                ", lastRedactionDate=" + lastRedactionDate +
                ", persistDate=" + persistDate +
                ", userSenderId=" + userSenderId +
                ", chatId=" + chatId +
                ", imageLink=" + imageLink +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDto that = (MessageDto) o;
        return Objects.equals(id, that.id) && Objects.equals(message, that.message) && Objects.equals(lastRedactionDate, that.lastRedactionDate) && Objects.equals(persistDate, that.persistDate) && Objects.equals(userSenderId, that.userSenderId) && Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, lastRedactionDate, persistDate, userSenderId, chatId);
    }
}
