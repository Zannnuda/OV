package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;

import java.util.List;
import java.util.Optional;

public interface SingleChatDtoService {
    Optional<SingleChatDto> findSingleChatDtoById(Long id);
    List<SingleChatDto> getAllSingleChatDto();
    PageDto<SingleChatDto, Object> getAllSingleChatDtoPagination(int page, int size, long userId);
}
