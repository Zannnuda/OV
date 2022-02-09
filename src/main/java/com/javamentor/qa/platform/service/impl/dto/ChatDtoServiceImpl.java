package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.ChatDtoDao;
import com.javamentor.qa.platform.models.dto.ChatDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.service.abstracts.dto.ChatDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatDtoServiceImpl implements ChatDtoService {

    private final ChatDtoDao chatDtoDao;
    private final PaginationService<ChatDto, Object> paginationServiceChatDto;

    @Autowired
    public ChatDtoServiceImpl(ChatDtoDao chatDtoDao, PaginationService<ChatDto, Object> paginationServiceChatDto) {
        this.chatDtoDao = chatDtoDao;
        this.paginationServiceChatDto = paginationServiceChatDto;
    }

    @Override
    public List<ChatDto> getAllChatsByUser(Long userId) {
        return chatDtoDao.getAllChatsByUser(userId);
    }

    @Override
    public PageDto<ChatDto, Object> getAllChatsByUserPagination(Long userId, int page, int size) {
        return paginationServiceChatDto.getPageDto(
                "paginationChatByUser", setPaginationParameters(userId, page, size)
        );
    }

    private Map<String, Object> setPaginationParameters(Long userId, int page, int size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("page", page);
        parameters.put("size", size);

        return parameters;
    }
}
