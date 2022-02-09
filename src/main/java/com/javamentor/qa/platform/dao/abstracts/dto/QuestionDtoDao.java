package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.QuestionDto;

import java.util.List;
import java.util.Optional;

public interface QuestionDtoDao {
     Optional<QuestionDto> getQuestionDtoById(Long id);

     List<Long> getPaginationQuestionIdsWithoutAnswerOrderByNew(int page, int size);

     List<Long> getPaginationQuestionIdsWithoutAnswerWithIgnoredTags(int page, int size, long id);

     List<Long> getPaginationQuestionIdsWithoutAnswer(int page, int size);

     List<Long> getPaginationQuestionIdsWithoutAnswerWithTrackedTags(int page, int size, long id);

     List<Long> getPaginationQuestionIdsOrderByNew(int page, int size);

     List<Long> getPaginationQuestionIdsWithoutAnswerOrderByVotes(int page, int size);

     List<Long> getPaginationQuestionIdsPopularWithTrackedTags(int page, int size, long id);

     List<Long> getPaginationQuestionIdsPopularWithIgnoredTags(int page, int size, long id);

    List<Long> getPaginationQuestionIdsWithFollowAndWithoutIgnoreTags(int page, int size, long id);
}
