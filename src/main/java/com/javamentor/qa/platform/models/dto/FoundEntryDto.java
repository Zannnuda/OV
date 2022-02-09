package com.javamentor.qa.platform.models.dto;

import com.javamentor.qa.platform.models.util.FoundEntryType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoundEntryDto {

    private Long questionId;
    private Long answerId;
    private String title;
    private String description;
    private Long votesCount;
    private Long answersCount;
    private List<TagDto> tags;
    private Long authorId;
    private String authorName;
    private LocalDateTime persistDate;
    private FoundEntryType type;

    @Override
    public String toString() {
        return "FoundEntryDto{" +
                "questionId=" + questionId +
                ", answerId=" + answerId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", votesCount=" + votesCount +
                ", answersCount=" + answersCount +
                ", tags=" + tags +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", persistDate=" + persistDate +
                ", type=" + type +
                '}';
    }
}
