package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.VoteAnswerDto;

import java.util.Optional;

public interface VoteAnswerDtoService {
    Optional<VoteAnswerDto> getVoteByQuestionIdAndUserId(Long questionId, Long userId);

    Optional<VoteAnswerDto> getVoteByAnswerIdAndUserId(Long answerId, Long userId);
}
