package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.MessageDtoDao;
import com.javamentor.qa.platform.models.dto.MessageDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.service.abstracts.dto.MessageDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageDtoServiceImpl implements MessageDtoService {

    private final MessageDtoDao messageDtoDao;
    private final PaginationService<MessageDto, Object> paginationServiceMessageDto;

    @Autowired
    public MessageDtoServiceImpl(MessageDtoDao messageDtoDao, PaginationService<MessageDto, Object> paginationServiceMessageDto) {
        this.messageDtoDao = messageDtoDao;
        this.paginationServiceMessageDto = paginationServiceMessageDto;
    }

    @Override
    public List<MessageDto> getAllMessageDtoByChatId(Long id) {
        return messageDtoDao.getAllMessageByChatIdDto(id);
    }

    @Override

    public PageDto<MessageDto, Object> getAllMessageDtoByChatIdPagination(int page, int size,Long chatId) {

        return paginationServiceMessageDto
                .getPageDto("paginationMessage", setPaginationParameters(page, size,chatId));
    }

    private Map<String, Object> setPaginationParameters(int page, int size, Long chatId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("chatId", chatId);
        return parameters;
    }
}
