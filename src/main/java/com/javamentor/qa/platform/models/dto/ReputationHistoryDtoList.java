package com.javamentor.qa.platform.models.dto;

import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReputationHistoryDtoList {
    private Long id;
    private Integer count;
    private ReputationType type;
    private LocalDateTime persistDate;

    @Override
    public String toString() {
        return "ReputationHistoryDtoList{" +
                "id=" + id +
                ", count=" + count +
                ", type=" + type +
                ", persistDate=" + persistDate +
                '}';
    }
}
