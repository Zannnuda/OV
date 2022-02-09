package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.QuestionDtoDao;
import com.javamentor.qa.platform.models.dto.PageDto;
import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.QuestionDtoPrincipal;
import com.javamentor.qa.platform.service.abstracts.dto.QuestionDtoService;
import com.javamentor.qa.platform.service.abstracts.dto.pagination.PaginationService;
import com.javamentor.qa.platform.service.impl.dto.pagination.question.PaginationQuestionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionDtoServiceImpl extends PaginationQuestionDtoService implements QuestionDtoService{

    private final QuestionDtoDao questionDtoDao;
    private final PaginationService<QuestionDtoPrincipal, Object> paginationService;

    @Autowired
    public QuestionDtoServiceImpl(QuestionDtoDao questionDtoDao, PaginationService<QuestionDtoPrincipal, Object> paginationService) {
        this.questionDtoDao = questionDtoDao;
        this.paginationService = paginationService;
    }

    @Transactional
    public Optional<QuestionDto> getQuestionDtoById(Long id) {
        return questionDtoDao.getQuestionDtoById(id);
    }

    public PageDto<QuestionDto, Object> getPagination(int page, int size) {
        return getPageDto(
                "paginationQuestion",
                setPaginationParameters(page, size, Optional.empty(), Optional.empty()));
    }

    public PageDto<QuestionDto, Object> getPaginationPopular(int page, int size, long days) {
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("days", days);
        return getPageDto("paginationQuestionByPopular", parameters);
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationPopularTrackedTag(int page, int size, long id){
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsPopularWithTrackedTags(page, size, id);
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);
        parameters.put("id", id);
        return getPageDto(
                "paginationQuestionByPopularTrackedTag",
                parameters);
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationPopularIgnoredTag(int page, int size, long id){
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsPopularWithIgnoredTags(page, size, id);
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);
        parameters.put("id", id);
        return getPageDto(
                "paginationQuestionByPopularIgnoredTag",
                parameters);
    }

    public PageDto<QuestionDto, Object> getPaginationOrderedNew(int page, int size) {
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsOrderByNew(page, size);
        parameters.put("ids", ids);
        return getPageDto(
                "paginationQuestionOrderByNew",
                parameters);
    }

    public PageDto<QuestionDto, Object> getPaginationWithoutAnswers(int page, int size) {
        return getPageDto(
                "paginationQuestionWithoutAnswers",
                setPaginationParameters(page, size, Optional.empty(), Optional.empty()));
    }

    public PageDto<QuestionDto, Object> getPaginationWithoutAnswersNoAnyAnswer(int page, int size) {
        return getPageDto(
                "paginationQuestionWithoutAnswersNoAnyAnswer",
                setPaginationParameters(page, size, Optional.empty(), Optional.empty()));
    }

    public PageDto<QuestionDto, Object> getPaginationWithGivenTags(int page, int size, List<Long> tagIds) {
        return getPageDto(
                "paginationQuestionWithGivenTags",
                setPaginationParameters(page, size, Optional.ofNullable(tagIds), Optional.empty()));
    }

    public PageDto<QuestionDto, Object> getPaginationWithoutTags(int page, int size, List<Long> tagIds) {
        return getPageDto(
                "paginationQuestionWithoutTags",
                setPaginationParameters(page, size, Optional.ofNullable(tagIds), Optional.empty()));
    }

    @Override
    public PageDto<QuestionDto, Object> getQuestionBySearchValue(String message, int page, int size) {
        return getPageDto(
                "paginationQuestionBySearchValue",
                setPaginationParameters(page, size, Optional.empty(), Optional.ofNullable(message)));
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationWithoutAnswerSortedByVotes(int page, int size) {
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsWithoutAnswerOrderByVotes(page, size);
        parameters.put("ids", ids);
        return getPageDto(
                "paginationQuestionWithoutAnswerSortedByVotes",
                parameters);
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationWithoutAnswersNew(int page, int size){
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsWithoutAnswerOrderByNew(page, size);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);

        return getPageDto(
                "paginationQuestionWithoutAnswersNew",
                parameters);
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationWithoutAnswersTrackedTag(int page, int size, long id){
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsWithoutAnswerWithTrackedTags(page, size, id);
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);
        parameters.put("id", id);
        return getPageDto(
                "paginationWithoutAnswersTrackedTag",
                parameters);
    }

    @Override
    public PageDto<QuestionDtoPrincipal, Object> getAllQuestionsOfPrincipalUserOrderByPersist(int page, int size, Long id) {
        return paginationService.getPageDto(
                "PaginationAllQuestionsPrincipalUserDao",
                setPaginationParametersForPrincipal(page, size, Optional.of(id)));
    }

    private Map<String, Object> setPaginationParameters(int page, int size, Optional<List<Long>> tagsIds, Optional<String> message) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("tagsIds", tagsIds.orElse(new ArrayList<>()));
        parameters.put("message", message.orElse(""));
        return parameters;
    }

    private Map<String, Object> setPaginationParametersForPrincipal(int page, int size, Optional<Long> userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("userId", userId.orElse(0L));
        return parameters;
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationWithoutAnswersIgnoredTags(int page, int size, long id){
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsWithoutAnswerWithIgnoredTags(page, size, id);
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);
        parameters.put("id", id);
        return getPageDto(
                "paginationWithoutAnswersIgnoredTag",
                parameters);
    }

    @Override
    public PageDto<QuestionDto, Object> getPaginationWithFollowAndWithoutIgnoreTags(int page, int size, long id) {
        List<Long> ids = questionDtoDao.getPaginationQuestionIdsWithFollowAndWithoutIgnoreTags(page, size, id);
        Map<String, Object> parameters = setPaginationParameters(page, size, Optional.empty(), Optional.empty());
        parameters.put("page", page);
        parameters.put("size", size);
        parameters.put("ids", ids);
        parameters.put("id", id);
        return getPageDto(
                "paginationWithTrackedWithoutIgnoredTag",
                parameters);
    }
}