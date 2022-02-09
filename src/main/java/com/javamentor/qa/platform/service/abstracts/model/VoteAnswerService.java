package com.javamentor.qa.platform.service.abstracts.model;


import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.user.User;
import org.springframework.http.ResponseEntity;


public interface VoteAnswerService extends ReadWriteService<VoteAnswer, Long> {

    boolean isUserAlreadyVotedIsThisQuestion(Question question, User user, Answer answer);

    void markHelpful(Question question, User user, Answer answer, boolean isHelpful);

    VoteAnswer answerUpVote(Question question, User user, Answer answer);

    VoteAnswer answerDownVote(Question question, User user, Answer answer);
}
