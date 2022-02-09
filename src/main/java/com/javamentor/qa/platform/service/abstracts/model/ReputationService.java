package com.javamentor.qa.platform.service.abstracts.model;

import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;

import java.util.Optional;

public interface ReputationService extends ReadWriteService<Reputation, Long> {
    Optional<Reputation> getReputationByAuthorId(Long userId);
    Reputation increaseReputationByQuestionVoteUp(Question question, User voteSender);
    Reputation decreaseReputationByQuestionVoteDown(Question question, User voteSender);
    Reputation increaseReputationByAnswerVoteUp(Answer answer, User voteSender);
    Reputation decreaseReputationByAnswerVoteDown(Answer answer, User voteSender);

}