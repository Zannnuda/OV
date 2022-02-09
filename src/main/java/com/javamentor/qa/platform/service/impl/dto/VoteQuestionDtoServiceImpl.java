package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.VoteQuestionDtoDao;
import com.javamentor.qa.platform.models.dto.VoteQuestionDto;
import com.javamentor.qa.platform.service.abstracts.dto.VoteQuestionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteQuestionDtoServiceImpl implements VoteQuestionDtoService {

    private final VoteQuestionDtoDao voteQuestionDtoDao;

    @Autowired
    public VoteQuestionDtoServiceImpl(VoteQuestionDtoDao voteQuestionDtoDao) {
        this.voteQuestionDtoDao = voteQuestionDtoDao;
    }

    @Override
    public Optional<VoteQuestionDto> getVoteByQuestionIdAndUserId(Long questionId, Long userId) {
        return voteQuestionDtoDao.getVoteByQuestionIdAndUserId(questionId, userId);
    }
}
