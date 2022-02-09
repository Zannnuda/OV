package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.QuestionViewedDao;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.QuestionViewed;
import com.javamentor.qa.platform.models.entity.question.Tag;
import com.javamentor.qa.platform.models.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionViewedDaoImpl extends ReadWriteDaoImpl<QuestionViewed, Long> implements QuestionViewedDao {

    EntityManager entityManager;

    @Autowired
    public QuestionViewedDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<QuestionViewed> getByQuestionIdAndUserId(Long questionId, Long userId) {
        TypedQuery<QuestionViewed> query = entityManager.createQuery(
                "from QuestionViewed where question.id =: questionId and user.id =: userId", QuestionViewed.class);
        query.setParameter("questionId", questionId);
        query.setParameter("userId", userId);

        return SingleResultUtil.getSingleResultOrNull(query);
    }

    @Override
    public List<QuestionViewed> getQuestionViewedListByQuestionId(Long questionId) {

        TypedQuery<QuestionViewed> query = entityManager.createQuery(
                "from QuestionViewed where question.id =: questionId", QuestionViewed.class);
        query.setParameter("questionId", questionId);
        System.out.println("ДО проверки -> " + questionId);
        if (query.getResultList().isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println("ВОООООООООООООООООООООООТ ОНОООО!!! -> " + questionId);
        return query.getResultList();
    }

}
