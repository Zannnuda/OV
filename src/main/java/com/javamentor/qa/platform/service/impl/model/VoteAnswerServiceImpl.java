package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReadWriteDao;
import com.javamentor.qa.platform.dao.abstracts.model.VoteAnswerDao;
import com.javamentor.qa.platform.exception.VoteException;
import com.javamentor.qa.platform.models.dto.VoteAnswerDto;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.dto.VoteAnswerDtoService;
import com.javamentor.qa.platform.service.abstracts.model.AnswerService;
import com.javamentor.qa.platform.service.abstracts.model.VoteAnswerService;
import com.javamentor.qa.platform.webapp.converters.VoteAnswerConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoteAnswerServiceImpl extends ReadWriteServiceImpl<VoteAnswer, Long> implements VoteAnswerService {

    private final AnswerService answerService;
    private final VoteAnswerDao voteAnswerDao;
    private final VoteAnswerDtoService voteAnswerDtoService;
    private final VoteAnswerConverter voteAnswerConverter;

    public VoteAnswerServiceImpl(ReadWriteDao<VoteAnswer, Long> readWriteDao,
                                 AnswerService answerService,
                                 VoteAnswerDao voteAnswerDao,
                                 VoteAnswerDtoService voteAnswerDtoService,
                                 VoteAnswerConverter voteAnswerConverter) {
        super(readWriteDao);
        this.answerService = answerService;
        this.voteAnswerDao = voteAnswerDao;
        this.voteAnswerDtoService = voteAnswerDtoService;
        this.voteAnswerConverter = voteAnswerConverter;
    }

    public void markHelpful(Question question, User user, Answer answer, boolean isHelpful) {

        boolean authorOfQuestion = voteAnswerDao.isAuthorOfQuestion(question.getId(), user.getId());
        if (authorOfQuestion) {
            answer.setIsHelpful(isHelpful);
            answer.setDateAcceptTime(LocalDateTime.now());
            answerService.update(answer);
        }
    }

    @Override
    public boolean isUserAlreadyVotedIsThisQuestion(Question question, User user, Answer answer) {

        return voteAnswerDao.isUserAlreadyVotedIsThisQuestion(question, user, answer);
    }

    @Override
    @Transactional
    public VoteAnswer answerUpVote(Question question, User user, Answer answer) {

        VoteAnswer voteAnswer = null;

        if (isUserAlreadyVotedIsThisQuestion(question, user, answer)) {
            Optional<VoteAnswerDto> optionalVoteAnswer = voteAnswerDtoService.getVoteByAnswerIdAndUserId(answer.getId(), user.getId());
            if (optionalVoteAnswer.isPresent()) {
                int voteValue = optionalVoteAnswer.get().getVote();
                if (voteValue == 1) {
                    voteAnswerDao.deleteById(optionalVoteAnswer.get().getId());
                    markHelpful(question, user, answer, false);
                } else if (voteValue == -1) {
                    voteAnswerDao.deleteById(optionalVoteAnswer.get().getId());
                    voteAnswer = new VoteAnswer(user, answer, 1);
                    voteAnswerDao.persist(voteAnswer);
                    markHelpful(question, user, answer, true);
                }
                return voteAnswer;
            }
            throw new VoteException("Can't change vote");
        }
        markHelpful(question, user, answer, true);

        voteAnswer = new VoteAnswer(user, answer, 1);
        voteAnswerDao.persist(voteAnswer);

        return voteAnswer;
    }

    @Override
    @Transactional
    public VoteAnswer answerDownVote(Question question, User user, Answer answer) {

        VoteAnswer voteAnswer = null;

        if (isUserAlreadyVotedIsThisQuestion(question, user, answer)) {
            Optional<VoteAnswerDto> optionalVoteAnswer = voteAnswerDtoService.getVoteByAnswerIdAndUserId(answer.getId(), user.getId());
            if (optionalVoteAnswer.isPresent()) {
                int voteValue = optionalVoteAnswer.get().getVote();
                if (voteValue == -1) {
                    voteAnswerDao.deleteById(optionalVoteAnswer.get().getId());
                } else if (voteValue == 1) {
                    voteAnswerDao.deleteById(optionalVoteAnswer.get().getId());
                    voteAnswer = new VoteAnswer(user, answer, -1);
                    voteAnswerDao.persist(voteAnswer);
                }
                markHelpful(question, user, answer, false);
                return voteAnswer;
            }
            throw new VoteException("Can't change vote");
        }

        voteAnswer = new VoteAnswer(user, answer, -1);
        voteAnswerDao.persist(voteAnswer);

        return voteAnswer;
    }
}