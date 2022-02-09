package com.javamentor.qa.platform.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatDto implements Serializable {

    private Long id;
    private Long chatId;
    private String title;
    private Set<UserDto> users;

    @Override
    public String toString() {
        return "GroupChatDto{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", title='" + title + '\'' +
                ", users=" + users +
                '}';
    }
}
