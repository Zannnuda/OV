package com.javamentor.qa.platform.models.dto;

import com.javamentor.qa.platform.models.entity.chat.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private Long id;
    private String title;
    private LocalDateTime persistDate;
    private ChatType chatType;


    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", persistDate=" + persistDate +
                ", chatType=" + chatType +
                '}';
    }
}
