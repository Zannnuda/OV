package com.javamentor.qa.platform.webapp.converters;

import com.javamentor.qa.platform.dao.abstracts.model.QuestionViewedDao;
import com.javamentor.qa.platform.models.dto.QuestionCreateDto;
import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.QuestionUpdateDto;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class QuestionConverter {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionViewedDao questionViewedDao;

    @Mapping(source = "question.id", target = "id")
    @Mapping(source = "question.user.id", target = "authorId")
    @Mapping(source = "question.title", target = "title")
    @Mapping(source = "question.description", target = "description")
    @Mapping(source = "question.user.fullName", target = "authorName")
    @Mapping(source = "question.user.imageLink", target = "authorImage")
    @Mapping(expression = "java(this.getViewsAmount(question))", target = "viewCount")
    @Mapping(source = "question.tags", target = "listTagDto")
    @Mapping(source = "question.persistDateTime", target = "persistDateTime")
    @Mapping(source = "question.lastUpdateDateTime", target = "lastUpdateDateTime")
    @Mapping(target = "countValuable", constant = "0")
    public abstract QuestionDto questionToQuestionDto(Question question);

    @Mapping(target = "user", source = "userId",qualifiedByName = "mapUser")
    public abstract Question questionCreateDtoToQuestion(QuestionCreateDto questionCreateDto);

    @Named("mapUser")
    public User mapUser(Long id) {
        return userService.getById(id).get();
    }

    @Mapping(source = "questionUpdateDto.title", target ="title")
    @Mapping(source = "questionUpdateDto.description", target ="description")
    public abstract Question questionUpdateDtoToQuestion(Question questionFromDB, QuestionUpdateDto questionUpdateDto);

    public int getViewsAmount (Question question) {
        return questionViewedDao.getQuestionViewedListByQuestionId(question.getId()).size();
    }
}

