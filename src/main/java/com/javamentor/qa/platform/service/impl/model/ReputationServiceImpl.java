package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReputationDao;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import com.javamentor.qa.platform.service.abstracts.model.ReputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class ReputationServiceImpl extends ReadWriteServiceImpl<Reputation, Long> implements ReputationService {

    private final ReputationDao reputationDao;


    private final Random r;
    private final int questionVotePoints = 20;
    private final int answerVotePoints = 20;

    @Autowired
    public ReputationServiceImpl(ReputationDao reputationDao) {
        super(reputationDao);
        r = new Random();
        this.reputationDao = reputationDao;

    }

    @Override
    public Optional<Reputation> getReputationByAuthorId(Long userId) {
        return reputationDao.getReputationByAuthorId(userId);
    }

    @Override
    public void persist(Reputation reputation) {
        reputation.setCount(r.nextInt(100));
        super.persist(reputation);
    }

    @Override
    public Reputation increaseReputationByQuestionVoteUp(Question question, User voteSender) {
        Reputation reputation = new Reputation();
        reputation.setType(ReputationType.VoteQuestion);
        reputation.setQuestion(question);
        reputation.setAuthor(question.getUser());
        reputation.setSender(voteSender);
        reputation.setCount(questionVotePoints);

        Optional<Reputation> existedReputation = reputationDao.
                getReputationByQuestionVoteSenderId(voteSender.getId(), question.getId());


        if (existedReputation.isPresent()) {
            super.delete(existedReputation.get());
        }
        super.persist(reputation);

        return reputation;
    }

    @Override
    public Reputation decreaseReputationByQuestionVoteDown(Question question, User voteSender) {
        Reputation reputation = new Reputation();
        reputation.setType(ReputationType.VoteQuestion);
        reputation.setQuestion(question);
        reputation.setAuthor(question.getUser());
        reputation.setSender(voteSender);
        reputation.setCount(-questionVotePoints);

        Optional<Reputation> existedReputation = reputationDao.
                getReputationByQuestionVoteSenderId(voteSender.getId(), question.getId());

        if (existedReputation.isPresent()) {
            super.delete(existedReputation.get());
        }
        super.persist(reputation);

        return reputation;
    }

    @Override
    public Reputation increaseReputationByAnswerVoteUp(Answer answer, User voteSender) {
        Reputation reputation = new Reputation();
        reputation.setType(ReputationType.VoteAnswer);
        reputation.setAnswer(answer);
        reputation.setAuthor(answer.getUser());
        reputation.setSender(voteSender);
        reputation.setCount(questionVotePoints);

        Optional<Reputation> existedReputation = reputationDao.
                getReputationByAnswerVoteSenderId(voteSender.getId(), answer.getId());

        if (existedReputation.isPresent()) {
            super.delete(existedReputation.get());
        }
        super.persist(reputation);

        return reputation;
    }

    @Override
    public Reputation decreaseReputationByAnswerVoteDown(Answer answer, User voteSender) {
        Reputation reputation = new Reputation();
        reputation.setType(ReputationType.VoteAnswer);
        reputation.setAnswer(answer);
        reputation.setAuthor(answer.getUser());
        reputation.setSender(voteSender);
        reputation.setCount(-questionVotePoints);

        Optional<Reputation> existedReputation = reputationDao.
                getReputationByAnswerVoteSenderId(voteSender.getId(), answer.getId());

        if (existedReputation.isPresent()) {
            super.delete(existedReputation.get());
        }
        super.persist(reputation);

        return reputation;
    }
}
