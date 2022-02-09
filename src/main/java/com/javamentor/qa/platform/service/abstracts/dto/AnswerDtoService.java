package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.AnswerDto;
import com.javamentor.qa.platform.models.dto.PageDto;

import java.util.List;

public interface AnswerDtoService {

    List<AnswerDto> getAllAnswersByQuestionId(Long questionId);

    boolean isUserAlreadyAnsweredToQuestion(Long id, Long questionId);

    PageDto<AnswerDto, Object> getAllAnswersOfPrincipalUserOrderByVotes(int page, int size, Long id);
}
