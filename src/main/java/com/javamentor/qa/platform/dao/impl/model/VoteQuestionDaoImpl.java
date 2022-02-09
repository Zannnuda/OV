package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.VoteQuestionDao;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.entity.question.VoteQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class VoteQuestionDaoImpl extends ReadWriteDaoImpl<VoteQuestion, Long> implements VoteQuestionDao {
    private final EntityManager entityManager;

    @Autowired
    public VoteQuestionDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<VoteQuestion> getVoteByQuestionIdAndUserId(Long questionId, Long userId) {
        TypedQuery<VoteQuestion> query = entityManager.createQuery(
                "FROM VoteQuestion WHERE user.id =: userId AND question.id =: questionId", VoteQuestion.class)
                .setParameter("userId", userId)
                .setParameter("questionId", questionId);
        return SingleResultUtil.getSingleResultOrNull(query);
    }
}
