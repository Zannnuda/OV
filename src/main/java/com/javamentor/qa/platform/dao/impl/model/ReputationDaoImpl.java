package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReputationDao;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class ReputationDaoImpl extends ReadWriteDaoImpl<Reputation, Long> implements ReputationDao {

    private final EntityManager entityManager;

    @Autowired
    public ReputationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Reputation> getReputationByAuthorId(Long userId) { // transfer to dtodao?

        TypedQuery<Reputation> query = entityManager.createQuery("FROM Reputation WHERE author.id =: authorId", Reputation.class)
                .setParameter("authorId", userId);

        return SingleResultUtil.getSingleResultOrNull(query);
    }

    @Override
    public Optional<Reputation> getReputationByQuestionVoteSenderId(Long userId, Long questionId) {
        { // transfer to dtodao?
            TypedQuery<Reputation> query = entityManager.createQuery(
                    "FROM Reputation WHERE sender.id =: senderId" +
                            " AND question.id =: quiestionId", Reputation.class)
                    .setParameter("senderId", userId)
                    .setParameter("questionId", questionId);
            return SingleResultUtil.getSingleResultOrNull(query);
        }
    }

    @Override
    public Optional<Reputation> getReputationByAnswerVoteSenderId(Long userId, Long answerId) {
        { // transfer to dtodao?
            TypedQuery<Reputation> query = entityManager.createQuery(
                    "FROM Reputation WHERE sender.id =: senderId" +
                            " AND answer.id =: answerId", Reputation.class)
                    .setParameter("senderId", userId)
                    .setParameter("answerId", answerId);
            return SingleResultUtil.getSingleResultOrNull(query);
        }
    }


}
