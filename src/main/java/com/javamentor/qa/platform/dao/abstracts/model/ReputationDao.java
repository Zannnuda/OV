package com.javamentor.qa.platform.dao.abstracts.model;

import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;

import java.util.Optional;

public interface ReputationDao extends ReadWriteDao<Reputation, Long> {
    Optional<Reputation> getReputationByAuthorId(Long userId);
    Optional<Reputation> getReputationByQuestionVoteSenderId(Long userId, Long questionId);
    Optional<Reputation> getReputationByAnswerVoteSenderId(Long userId, Long answerId);


}
