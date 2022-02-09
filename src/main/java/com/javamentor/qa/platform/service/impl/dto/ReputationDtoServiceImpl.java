package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.ReputationHistoryDtoList;
import com.javamentor.qa.platform.service.abstracts.dto.ReputationDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReputationDtoServiceImpl implements ReputationDtoService {

    private final PaginationService<ReputationHistoryDtoList, Object> paginationService;

    public ReputationDtoServiceImpl(PaginationService<ReputationHistoryDtoList, Object> paginationService) {
        this.paginationService = paginationService;
    }

    @Override
    public PageDto<ReputationHistoryDtoList, Object> getPageReputationHistoryDto(int page, int size, Long id) {
        return paginationService.getPageDto(
                "paginationReputationHistory",
                setPaginationParameters(page, size, id));
    }

    private Map<String, Object> setPaginationParameters(int page, int size, Long id) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("reputationId", id);
        return parameters;
    }
}
