package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.SingleChatDto;

import java.util.List;
import java.util.Optional;

public interface SingleChatDtoDao {

    Optional<SingleChatDto> findSingleChatDtoById(Long id);
    List<SingleChatDto> getAllSingleChatDto();

}
