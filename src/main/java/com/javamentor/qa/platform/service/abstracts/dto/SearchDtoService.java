package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.FoundEntryDto;
import com.javamentor.qa.platform.models.dto.PageDto;

public interface SearchDtoService {
    PageDto<FoundEntryDto, Object> getSearchDtoPagination(String query, int page, int size, String sort, String order);
}
