package com.javamentor.qa.platform.service.impl.dto;


import com.javamentor.qa.platform.dao.abstracts.dto.VoteAnswerDtoDao;
import com.javamentor.qa.platform.models.dto.VoteAnswerDto;
import com.javamentor.qa.platform.service.abstracts.dto.VoteAnswerDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteAnswerDtoServiceImpl implements VoteAnswerDtoService {

    private final VoteAnswerDtoDao voteAnswerDtoDao;

    @Autowired
    public VoteAnswerDtoServiceImpl(VoteAnswerDtoDao voteAnswerDtoDao){
        this.voteAnswerDtoDao = voteAnswerDtoDao;
    }

    @Override
    public Optional<VoteAnswerDto> getVoteByQuestionIdAndUserId(Long questionId, Long userId) {
        return voteAnswerDtoDao.getVoteByQuestionIdAndUserId(questionId, userId);
    }

    @Override
    public Optional<VoteAnswerDto> getVoteByAnswerIdAndUserId(Long answerId, Long userId) {
        return voteAnswerDtoDao.getVoteByAnswerIdAndUserId(answerId, userId);
    }
}
