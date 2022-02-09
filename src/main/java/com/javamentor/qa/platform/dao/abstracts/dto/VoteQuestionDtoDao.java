package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.VoteQuestionDto;

import java.util.Optional;

public interface VoteQuestionDtoDao {

    Optional<VoteQuestionDto> getVoteByQuestionIdAndUserId(Long questionId, Long userId);
}
