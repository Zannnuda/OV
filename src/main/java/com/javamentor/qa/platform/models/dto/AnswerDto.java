package com.javamentor.qa.platform.models.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto implements Serializable {
    private Long id;
    private Long userId;
    private Long questionId;
    private String body;
    private LocalDateTime persistDate;
    private Boolean isHelpful;
    private LocalDateTime dateAccept;
    private Long countValuable;
    private String image;
    private String nickName;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", userId=" + userId +
                ", questionId=" + questionId +
                ", body=" + body +
                ", persistDate=" + persistDate +
                ", isHelpful=" + isHelpful +
                ", dateAccept=" + dateAccept +
                ", countValuable=" + countValuable +
                ", image=" + image +
                ", nickName=" + nickName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(id, answerDto.id) && Objects.equals(userId, answerDto.userId) && Objects.equals(questionId, answerDto.questionId) && Objects.equals(body, answerDto.body) && Objects.equals(persistDate, answerDto.persistDate) && Objects.equals(isHelpful, answerDto.isHelpful) && Objects.equals(dateAccept, answerDto.dateAccept) && Objects.equals(image, answerDto.image) && Objects.equals(nickName, answerDto.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, questionId, body, persistDate, isHelpful, dateAccept, image, nickName);
    }
}
