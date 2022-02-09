package com.javamentor.qa.platform.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDtoPrincipal implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime persistDate;
    private String description;
    private Long countValuable;

    @Override
    public String toString() {
        return "QuestionDtoPrincipal{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", persistDate=" + persistDate +
                ", description='" + description + '\'' +
                ", countValuable=" + countValuable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionDtoPrincipal that = (QuestionDtoPrincipal) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(title, that.title) && Objects.equals(persistDate, that.persistDate) && Objects.equals(description, that.description) && Objects.equals(countValuable, that.countValuable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, title, persistDate, description, countValuable);
    }
}
