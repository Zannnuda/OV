package com.javamentor.qa.platform.webapp.converters;


import com.javamentor.qa.platform.models.dto.VoteQuestionDto;
import com.javamentor.qa.platform.models.entity.question.VoteQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class VoteQuestionConverter {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "localDateTime", target = "persistDateTime")
    @Mapping(source = "vote", target = "vote")
    public abstract VoteQuestionDto voteQuestionToVoteQuestionDto (VoteQuestion voteQuestion);
}

