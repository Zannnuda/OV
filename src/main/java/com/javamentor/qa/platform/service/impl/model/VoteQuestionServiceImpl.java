package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.VoteQuestionDao;
import com.javamentor.qa.platform.exception.VoteException;
import com.javamentor.qa.platform.models.dto.VoteQuestionDto;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.VoteQuestion;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.dto.VoteQuestionDtoService;
import com.javamentor.qa.platform.service.abstracts.model.VoteQuestionService;
import com.javamentor.qa.platform.webapp.converters.VoteQuestionConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteQuestionServiceImpl extends ReadWriteServiceImpl<VoteQuestion, Long> implements VoteQuestionService {

    private final VoteQuestionDao voteQuestionDao;
    private final VoteQuestionDtoService voteQuestionDtoService;
    private final VoteQuestionConverter voteQuestionConverter;

    public VoteQuestionServiceImpl(VoteQuestionDao voteQuestionDao,
                                   VoteQuestionDao voteQuestionDao1, VoteQuestionDtoService voteQuestionDtoService,
                                   VoteQuestionConverter voteQuestionConverter) {
        super(voteQuestionDao);
        this.voteQuestionDao = voteQuestionDao1;
        this.voteQuestionDtoService = voteQuestionDtoService;
        this.voteQuestionConverter = voteQuestionConverter;
    }

    @Override
    public boolean isUserAlreadyVoted(Question question, User user) {
        List<VoteQuestion> list = question.getVoteQuestions();
        for (VoteQuestion voteQuestion : list) {
            if (voteQuestion.getUser().getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public VoteQuestion questionUpVote(Question question, User user) {

        VoteQuestion voteQuestion = null;

        if (isUserAlreadyVoted(question, user)) {
            Optional<VoteQuestionDto> optionalVoteQuestion = voteQuestionDtoService.getVoteByQuestionIdAndUserId(question.getId(), user.getId());
            if (optionalVoteQuestion.isPresent()) {
                int voteValue = optionalVoteQuestion.get().getVote();
                if (voteValue == 1) {
                    voteQuestionDao.deleteById(optionalVoteQuestion.get().getId());
                } else if (voteValue == -1) {
                    voteQuestionDao.deleteById(optionalVoteQuestion.get().getId());
                    voteQuestion = new VoteQuestion(user, question, 1);
                    voteQuestionDao.persist(voteQuestion);
                }
                return voteQuestion;
            }
            throw new VoteException("Can't change vote");
        }

        voteQuestion = new VoteQuestion(user, question, 1);
        voteQuestionDao.persist(voteQuestion);

        return voteQuestion;
    }

    @Override
    @Transactional
    public VoteQuestion questionDownVote(Question question, User user) {

        VoteQuestion voteQuestion = null;

        if (isUserAlreadyVoted(question, user)) {
            Optional<VoteQuestionDto> optionalVoteQuestion = voteQuestionDtoService.getVoteByQuestionIdAndUserId(question.getId(), user.getId());
            if (optionalVoteQuestion.isPresent()) {
                int voteValue = optionalVoteQuestion.get().getVote();
                if (voteValue == -1) {
                    voteQuestionDao.deleteById(optionalVoteQuestion.get().getId());
                } else if (voteValue == 1) {
                    voteQuestionDao.deleteById(optionalVoteQuestion.get().getId());
                    voteQuestion = new VoteQuestion(user, question, -1);
                    voteQuestionDao.persist(voteQuestion);
                }
                return voteQuestion;
            }
            throw new VoteException("Can't change vote");
        }

        voteQuestion = new VoteQuestion(user, question, -1);
        voteQuestionDao.persist(voteQuestion);

        return voteQuestion;
    }
}
