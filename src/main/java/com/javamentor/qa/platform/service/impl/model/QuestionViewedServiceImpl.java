package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.QuestionViewedDao;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.QuestionViewed;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.model.QuestionViewedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionViewedServiceImpl extends ReadWriteServiceImpl<QuestionViewed, Long> implements QuestionViewedService {

    private final QuestionViewedDao questionViewedDao;

    public QuestionViewedServiceImpl(QuestionViewedDao questionViewedDao) {
        super(questionViewedDao);
        this.questionViewedDao = questionViewedDao;
    }


    @Transactional
    @Override
    public void markQuestionAsViewed(Question question, User user) {

        Optional<QuestionViewed> optionalQuestionViewed =
                questionViewedDao.getByQuestionIdAndUserId(question.getId(), user.getId());

        if (!(optionalQuestionViewed.isPresent())) {
            QuestionViewed questionViewed = new QuestionViewed();
            questionViewed.setQuestion(question);
            questionViewed.setUser(user);

            questionViewedDao.persist(questionViewed);
        }
    }
}
