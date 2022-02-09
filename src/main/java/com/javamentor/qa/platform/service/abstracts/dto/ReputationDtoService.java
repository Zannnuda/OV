package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.ReputationHistoryDtoList;

public interface ReputationDtoService {

    PageDto<ReputationHistoryDtoList, Object> getPageReputationHistoryDto(int page, int size, Long id);

}
