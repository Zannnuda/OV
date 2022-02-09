package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.ChatDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;

import java.util.List;

public interface ChatDtoService {
    List<ChatDto> getAllChatsByUser(Long userId);
    PageDto<ChatDto, Object> getAllChatsByUserPagination(Long userId, int page, int size);
}
