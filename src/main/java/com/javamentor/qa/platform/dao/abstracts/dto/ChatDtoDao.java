package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.ChatDto;

import java.util.List;

public interface ChatDtoDao {
    List<ChatDto> getAllChatsByUser(Long userId);
}
