package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.MessageDto;
import com.javamentor.qa.platform.models.dto.PageDto;

import java.util.List;


public interface MessageDtoService {
    List<MessageDto> getAllMessageDtoByChatId(Long id);
    PageDto<MessageDto, Object> getAllMessageDtoByChatIdPagination(int page, int size,Long chatId);
}
