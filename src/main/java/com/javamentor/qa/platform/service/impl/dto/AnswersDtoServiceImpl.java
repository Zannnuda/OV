package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.AnswerDtoDao;
import com.javamentor.qa.platform.models.dto.AnswerDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.service.abstracts.dto.AnswerDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnswersDtoServiceImpl implements AnswerDtoService {

    private final AnswerDtoDao answerDtoDao;
    private final PaginationService<AnswerDto, Object> paginationService;

    @Autowired
    public AnswersDtoServiceImpl(AnswerDtoDao answerDtoDao,PaginationService<AnswerDto, Object> paginationService){
        this.answerDtoDao = answerDtoDao;
        this.paginationService = paginationService;
    }

    @Override
    public List<AnswerDto> getAllAnswersByQuestionId(Long questionId) {
        return answerDtoDao.getAllAnswersByQuestionId(questionId);
    }

    @Override
    public boolean isUserAlreadyAnsweredToQuestion(Long userId, Long questionId) {
        return answerDtoDao.isUserAlreadyAnsweredToQuestion(userId, questionId);
    }

    @Override
    public PageDto<AnswerDto, Object> getAllAnswersOfPrincipalUserOrderByVotes(int page, int size, Long id) {
        return paginationService.getPageDto(
                "PaginationAllAnswersPrincipalUserDao",
                setPaginationParameters(page, size, Optional.of(id)));
    }

    private Map<String, Object> setPaginationParameters(int page, int size, Optional<Long> userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("userId", userId.orElse(0L));
        return parameters;
    }
}
