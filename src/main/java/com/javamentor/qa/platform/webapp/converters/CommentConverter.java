package com.javamentor.qa.platform.webapp.converters;

import com.javamentor.qa.platform.models.dto.CommentDto;
import com.javamentor.qa.platform.models.entity.question.CommentQuestion;
import com.javamentor.qa.platform.models.entity.question.answer.CommentAnswer;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.service.abstracts.model.ReputationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


@Mapper(componentModel = "spring")
public abstract class CommentConverter {

    private ReputationService reputationService;

    @Autowired
    public void setReputationService(ReputationService reputationService) {
        this.reputationService = reputationService;
    }

    @Mapping(source = "comment", target = ".")
    @Mapping(source = "comment.persistDateTime", target = "persistDate")
    @Mapping(source = "comment.lastUpdateDateTime", target = "lastRedactionDate")
    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(source = "comment.user.fullName", target = "username")
    @Mapping(source = "comment.user.id", target = "reputation", qualifiedByName = "getReputationCount")
    public abstract CommentDto commentToCommentDTO(CommentQuestion commentQuestion);

    @Mapping(source = "comment", target = ".")
    @Mapping(source = "comment.persistDateTime", target = "persistDate")
    @Mapping(source = "comment.lastUpdateDateTime", target = "lastRedactionDate")
    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(source = "comment.user.fullName", target = "username")
    @Mapping(source = "comment.user.id", target = "reputation", qualifiedByName = "getReputationCount")
    public abstract CommentDto commentToCommentDTO(CommentAnswer commentAnswer);

    @Named("getReputationCount")
    public Integer getReputationCountByUserId(Long userId) {
        Optional<Reputation> rep = reputationService.getReputationByAuthorId(userId);
        if (rep.isPresent()) {
            return rep.get().getCount();
        }

        return 0;//Tuple in reputation table, that must be connected with this user not found
    }
}
