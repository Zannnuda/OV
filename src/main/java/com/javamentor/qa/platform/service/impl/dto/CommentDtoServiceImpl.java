package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.CommentDtoDao;
import com.javamentor.qa.platform.models.dto.CommentAnswerDto;
import com.javamentor.qa.platform.models.dto.CommentQuestionDto;
import com.javamentor.qa.platform.service.abstracts.dto.CommentDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentDtoServiceImpl implements CommentDtoService {

    private CommentDtoDao commentDtoDao;

    @Autowired
    public CommentDtoServiceImpl(CommentDtoDao commentDtoDao) {
        this.commentDtoDao = commentDtoDao;
    }

    @Override
    public List<CommentQuestionDto> getAllCommentsByQuestionId(Long questionId) {
        return commentDtoDao.getAllCommentsByQuestionId(questionId);
    }

    @Override
    public List<CommentAnswerDto> getAllCommentsByAnswerId(Long answerId) {
        return commentDtoDao.getAllCommentsByAnswerId(answerId);
    }

    @Override
    public boolean isUserAlreadyCommentedQuestion(Long userId, Long questionId) {
        return commentDtoDao.isUserAlreadyCommentedQuestion(userId, questionId);
    }

    @Override
    public boolean isUserAlreadyCommentedAnswer(Long userId, Long answerId) {
        return commentDtoDao.isUserAlreadyCommentedAnswer(userId, answerId);
    }
}
