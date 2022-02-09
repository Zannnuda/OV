package com.javamentor.qa.platform.models.dto;

import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto implements Serializable {
    private Long id;
    private Long userId;
    private String questionTitle;
    private Integer questionVotes;

    @Override
    public String toString() {
        return "BookmarkDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionVotes=" + questionVotes +
                '}';
    }
}
