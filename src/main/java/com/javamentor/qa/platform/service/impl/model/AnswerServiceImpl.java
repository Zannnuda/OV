package com.javamentor.qa.platform.service.impl.model;


import com.javamentor.qa.platform.dao.abstracts.model.AnswerDao;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.service.abstracts.model.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class AnswerServiceImpl extends ReadWriteServiceImpl<Answer, Long> implements AnswerService {

    @Autowired
    private AnswerDao answerDao;


     public AnswerServiceImpl(AnswerDao answerDao) {
         super(answerDao);
     }

    @Override
    @Transactional
    public void markAnswerAsDeleted(long answerId) {
        Answer answer = getById(answerId).get();
        answer.setIsDeletedByModerator(true);
        answerDao.update(answer);
    }
}

