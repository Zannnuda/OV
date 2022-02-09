package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.models.dto.FoundEntryDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.service.abstracts.dto.SearchDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchDtoServiceImpl implements SearchDtoService {

    private PaginationService<FoundEntryDto, Object> paginationServiceFoundEntryDto;

    @Autowired
    public SearchDtoServiceImpl(PaginationService<FoundEntryDto, Object> paginationServiceFoundEntryDto) {
        this.paginationServiceFoundEntryDto = paginationServiceFoundEntryDto;
    }

    @Override
    public PageDto<FoundEntryDto, Object> getSearchDtoPagination(String query, int page, int size, String sort, String order) {
        return paginationServiceFoundEntryDto.getPageDto(
                "paginationSearch",
                setPaginationParameters(query, page, size, sort, order)
        );
    }

    private Map<String, Object> setPaginationParameters(String query, int page, int size, String sort, String order) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("query", query);
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("sort", sort);
        parameters.put("order", order);

        return parameters;
    }
}
