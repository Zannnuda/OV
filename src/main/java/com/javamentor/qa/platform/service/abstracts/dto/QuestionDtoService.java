package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.QuestionDtoPrincipal;

import java.util.List;
import java.util.Optional;

public interface QuestionDtoService {
    Optional<QuestionDto> getQuestionDtoById(Long id);

    PageDto<QuestionDto, Object> getPagination(int page, int size);

    PageDto<QuestionDto, Object> getPaginationPopular(int page, int size, long days);

    PageDto<QuestionDto, Object> getPaginationPopularTrackedTag(int page, int size, long id);

    PageDto<QuestionDto, Object> getPaginationPopularIgnoredTag(int page, int size, long id);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswers(int page, int size);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswersNoAnyAnswer(int page, int size);

    PageDto<QuestionDto, Object> getPaginationOrderedNew(int page, int size);

    PageDto<QuestionDto, Object> getPaginationWithGivenTags(int page, int size, List<Long> tagIds);

    PageDto<QuestionDto, Object> getPaginationWithoutTags(int page, int size, List<Long> tagIds);

    PageDto<QuestionDto, Object> getQuestionBySearchValue(String message, int page, int size);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswersNew(int page, int size);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswersIgnoredTags(int page, int size, long id);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswerSortedByVotes(int page, int size);

    PageDto<QuestionDto, Object> getPaginationWithoutAnswersTrackedTag(int page, int size, long id);

    PageDto<QuestionDtoPrincipal, Object> getAllQuestionsOfPrincipalUserOrderByPersist(int page, int size, Long id);

    PageDto<QuestionDto, Object> getPaginationWithFollowAndWithoutIgnoreTags(int page, int size, long id);
}
