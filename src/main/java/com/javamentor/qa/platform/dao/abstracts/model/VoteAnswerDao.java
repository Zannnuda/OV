package com.javamentor.qa.platform.dao.abstracts.model;

import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.user.User;

import java.util.Optional;

public interface VoteAnswerDao extends ReadWriteDao<VoteAnswer, Long>{

     boolean isUserAlreadyVotedIsThisQuestion(Question question, User user, Answer answer);

     boolean isAuthorOfQuestion(Long questionId, Long userId);

     Optional<VoteAnswer> getVoteByAnswerIdAndUserId(Long answerId, Long userId);
}
