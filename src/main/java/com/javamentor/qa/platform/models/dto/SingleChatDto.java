package com.javamentor.qa.platform.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleChatDto implements Serializable {

    private Long id;
    private String title;
    private Long userOneId;
    private Long userTwoId;
    private String nickname;
    private String imageLink;
    private String message;
    private Long userSenderId;
    private Timestamp lastRedactionDate;

    @Override
    public String toString() {
        return "SingleChatDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userOneId=" + userOneId +
                ", userTwoId=" + userTwoId +
                ", nickname='" + nickname + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", message='" + message + '\'' +
                ", lastRedactionDate=" + lastRedactionDate +
                ", userSenderId=" + userSenderId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleChatDto that = (SingleChatDto) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(userOneId, that.userOneId) && Objects.equals(userTwoId, that.userTwoId) && Objects.equals(nickname, that.nickname) && Objects.equals(imageLink, that.imageLink) && Objects.equals(message, that.message) && Objects.equals(lastRedactionDate, that.lastRedactionDate) && Objects.equals(userSenderId, that.userSenderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, userOneId, userTwoId, nickname, imageLink, message, lastRedactionDate, userSenderId);
    }
}
