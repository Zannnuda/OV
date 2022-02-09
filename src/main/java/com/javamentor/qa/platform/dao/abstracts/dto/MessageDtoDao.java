package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.MessageDto;

import java.util.List;


public interface MessageDtoDao {
    List<MessageDto> getAllMessageByChatIdDto(Long chatId);
}
