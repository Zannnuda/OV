package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.AnswerDto;

import java.util.List;

public interface AnswerDtoDao {
    List<AnswerDto> getAllAnswersByQuestionId(Long questionId);

    boolean isUserAlreadyAnsweredToQuestion(Long userId, Long questionId);
}
