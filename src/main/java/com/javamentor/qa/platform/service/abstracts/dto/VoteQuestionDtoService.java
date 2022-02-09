package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.VoteQuestionDto;

import java.util.Optional;

public interface VoteQuestionDtoService {

    Optional<VoteQuestionDto> getVoteByQuestionIdAndUserId(Long questionId, Long userId);
}
