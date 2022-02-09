package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.SingleChatDtoDao;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;
import com.javamentor.qa.platform.service.abstracts.dto.SingleChatDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SingleChatDtoServiceImpl implements SingleChatDtoService {

    private SingleChatDtoDao singleChatDtoDao;
    private PaginationService<SingleChatDto, Object> paginationServiceSingleChatDto;

    @Autowired
    public SingleChatDtoServiceImpl(SingleChatDtoDao singleChatDtoDao, PaginationService<SingleChatDto, Object> paginationServiceSingleChatDto) {
        this.singleChatDtoDao = singleChatDtoDao;
        this.paginationServiceSingleChatDto = paginationServiceSingleChatDto;
    }

    @Override
    public Optional<SingleChatDto> findSingleChatDtoById(Long id) {
        return singleChatDtoDao.findSingleChatDtoById(id);
    }

    @Override
    public List<SingleChatDto> getAllSingleChatDto() {
        return singleChatDtoDao.getAllSingleChatDto();
    }

    @Override
    public PageDto<SingleChatDto, Object> getAllSingleChatDtoPagination(int page, int size, long userId) {
        return paginationServiceSingleChatDto.getPageDto(
                "paginationSingleChat", setPaginationParameters(page, size, userId)
        );
    }

    private Map<String, Object> setPaginationParameters(int page, int size, long userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("userId", userId);
        return parameters;
    }


}
