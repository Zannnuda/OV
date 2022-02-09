package com.javamentor.qa.platform.webapp.controllers;

import com.javamentor.qa.platform.dao.abstracts.dto.VoteQuestionDtoDao;
import com.javamentor.qa.platform.exception.VoteException;
import com.javamentor.qa.platform.models.dto.*;
import com.javamentor.qa.platform.models.entity.question.CommentQuestion;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.QuestionViewed;
import com.javamentor.qa.platform.models.entity.question.VoteQuestion;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import com.javamentor.qa.platform.models.util.OnCreate;
import com.javamentor.qa.platform.security.util.SecurityHelper;
import com.javamentor.qa.platform.service.abstracts.dto.*;
import com.javamentor.qa.platform.service.abstracts.model.*;
import com.javamentor.qa.platform.webapp.converters.*;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/question")
@Api(value = "QuestionApi")
public class QuestionController {

    private final QuestionService questionService;
    private final TagService tagService;
    private final SecurityHelper securityHelper;
    private final QuestionDtoService questionDtoService;
    private final CommentQuestionService commentQuestionService;
    private final CommentConverter commentConverter;
    private final CommentDtoService commentDtoService;
    private final QuestionConverter questionConverter;
    private final UserService userService;
    private final VoteQuestionService voteQuestionService;
    private final QuestionViewedService questionViewedService;
    private final ReputationService reputationService;
    private final VoteQuestionConverter voteQuestionConverter;
    private final VoteQuestionDtoDao voteQuestionDtoDao;

    private static final int MAX_ITEMS_ON_PAGE = 100;

    @Autowired
    public QuestionController(QuestionService questionService,
                              TagService tagService,
                              SecurityHelper securityHelper,
                              QuestionDtoService questionDtoService,
                              CommentQuestionService commentQuestionService,
                              CommentConverter commentConverter,
                              CommentDtoService commentDtoService,
                              QuestionConverter questionConverter,
                              UserService userService,
                              VoteQuestionService voteQuestionService,
                              QuestionViewedService questionViewedService,
                              ReputationService reputationService,
                              VoteQuestionConverter voteQuestionConverter, VoteQuestionDtoDao voteQuestionDtoDao) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.securityHelper = securityHelper;
        this.questionDtoService = questionDtoService;
        this.commentQuestionService = commentQuestionService;
        this.commentConverter = commentConverter;
        this.commentDtoService = commentDtoService;
        this.questionConverter = questionConverter;
        this.userService = userService;
        this.voteQuestionService = voteQuestionService;
        this.questionViewedService = questionViewedService;
        this.reputationService = reputationService;
        this.voteQuestionConverter = voteQuestionConverter;
        this.voteQuestionDtoDao = voteQuestionDtoDao;
    }

    @DeleteMapping("/{id}/delete")
    @ApiOperation(value = "Delete question", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deletes the question.", response = String.class),
            @ApiResponse(code = 400, message = "Wrong ID", response = String.class)
    })
    public ResponseEntity<String> deleteQuestionById(@ApiParam(name = "id") @PathVariable Long id) {

        Optional<Question> question = questionService.getById(id);
        if (question.isPresent()) {
            questionService.delete(question.get());
            return ResponseEntity.ok("Question was deleted");
        } else {
            return ResponseEntity.badRequest().body("Wrong ID");
        }
    }

    @SneakyThrows
    @PatchMapping("/{questionId}/tag/add")
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tags were added", response = String.class),
            @ApiResponse(code = 400, message = "Question not found", response = String.class)
    })
    public ResponseEntity<?> setTagForQuestion(
            @ApiParam(name = "questionId", value = "type Long", required = true, example = "0")
            @PathVariable Long questionId,
            @ApiParam(name = "tagId", value = "type List<Long>", required = true)
            @RequestBody List<Long> tagId) {

        if (questionId == null) {
            return ResponseEntity.badRequest().body("Question id is null");
        }

        Optional<Question> question = questionService.getById(questionId);
        if (!question.isPresent()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        if (tagService.existsByAllIds(tagId)) {
            tagService.addTagToQuestion(tagId, question.get());
            return ResponseEntity.ok().body("Tags were added");
        }

        return ResponseEntity.badRequest().body("Tag not found");
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "get QuestionDto", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the QuestionDto", response = QuestionDto.class),
            @ApiResponse(code = 400, message = "Question not found", response = String.class)
    })

    public ResponseEntity<?> getQuestionById(
            @ApiParam(name = "id", value = "type Long", required = true, example = "0")
            @PathVariable Long id) {

        User principal = securityHelper.getPrincipal();
        Question question = questionService.getById(id).get();

        questionViewedService.markQuestionAsViewed(question, principal);

        System.out.println("Начало метода");
        Optional<QuestionDto> questionDto = questionDtoService.getQuestionDtoById(id);


        return questionDto.isPresent() ? ResponseEntity.ok(questionDto.get()) :
                ResponseEntity.badRequest().body("Question not found");
    }

    @GetMapping(path = "/",
            params = {"page", "size"}
    )
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> findPagination(

            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPagination(page, size);

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/popular", params = {"page", "size"})
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination popular List<QuestionDto>"),
    })
    public ResponseEntity<?> findPaginationPopular(

            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService
                .getPaginationPopular(page, size, LocalDateTime.now().getLong(ChronoField.EPOCH_DAY));

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/popular/trackedTag", params = {"page", "size"})
    @ApiOperation(value = "Return Questions sorted by trackedTag in descending order of popularity")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsPopularTrackedTag(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService
                .getPaginationPopularTrackedTag(page, size, securityHelper.getPrincipal().getId());
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/popular/ignoredTag", params = {"page", "size"})
    @ApiOperation(value = "Return Questions sorted by ignoredTag in descending order of popularity")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionPopularIgnoredTag(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService
                .getPaginationPopularIgnoredTag(page, size, securityHelper.getPrincipal().getId());
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/popular/week", params = {"page", "size"})
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination popular List<QuestionDto>"),
    })
    public ResponseEntity<?> findPaginationPopularWeek(

            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationPopular(page, size, 7L);

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/popular/month", params = {"page", "size"})
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination popular List<QuestionDto>"),
    })
    public ResponseEntity<?> findPaginationPopularMonth(

            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationPopular(page, size, 30L);

        return ResponseEntity.ok(resultPage);
    }


    @PostMapping("/add")
    @Validated(OnCreate.class)
    @ResponseBody
    @ApiOperation(value = "add Question", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Add Question", response = Question.class),
            @ApiResponse(code = 400, message = "Question not add", response = String.class)
    })
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionCreateDto questionCreateDto) {
        User user = securityHelper.getPrincipal();
        Integer count = 5;

        if (!userService.existsById(questionCreateDto.getUserId())) {
            return ResponseEntity.badRequest().body("questionCreateDto.userId dont`t exist");
        }

        Question question = questionConverter.questionCreateDtoToQuestion(questionCreateDto);
        questionService.persist(question);
        Reputation reputation = new Reputation();
        reputation.setCount(count);
        reputation.setQuestion(question);
        reputation.setAuthor(user);
        reputation.setType(ReputationType.Question);
        reputationService.update(reputation);

        QuestionDto questionDtoNew = questionConverter.questionToQuestionDto(question);
        return ResponseEntity.ok(questionDtoNew);
    }


    @GetMapping(value = "order/new", params = {"page", "size"})
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination popular List<QuestionDto>"),
    })
    public ResponseEntity<?> findPaginationOrderedNew(

            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationOrderedNew(page, size);


        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/withoutAnswer", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers (where the author did not mark is_helpful)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswer(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {


        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswers(page, size);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/withoutAnswer/noAnyAnswer", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers (not a single answer written)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswerNoAnyAnswer(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {


        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswersNoAnyAnswer(page, size);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/withoutAnswer/votes", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers sorted by votes")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswerSortedByVotes(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswerSortedByVotes(page, size);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/withTags", params = {"page", "size", "tagIds"})
    @ApiOperation(value = "Return questions that include all given tags")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Return the pagination PageDto", response = PageDto.class),
            @ApiResponse(code = 400, message = "QuestionList with given TagIds not found.")
    })
    public ResponseEntity<?> getQuestionsWithGivenTags(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size,
            @ApiParam(name = "tagIds", required = true, type = "List<Long>")
            @RequestParam("tagIds") List<Long> tagIds
    ) {
        if (size <= 0 || page <= 0 || size > MAX_ITEMS_ON_PAGE) {
            ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithGivenTags(page, size, tagIds);

        if (resultPage.getItems().isEmpty()) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping(value = "/withoutTags", params = {"page", "size"})
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination without tags List<QuestionDto>"),
            @ApiResponse(code = 400, message = "QuestionList with given TagIds not found.")
    })
    public ResponseEntity<?> findPaginationWithoutTags(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size,
            @ApiParam(name = "tagIds", required = true, type = "List<Long>", value = "List of id tags to be deleted")
            @RequestBody List<Long> tagIds) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutTags(page, size, tagIds);

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/tracked-ignored")
    @ApiOperation(value = "Return object(PageDto<QuestionDto, Object>)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination with follow tags and without ignore tags List<QuestionDto>"),
            @ApiResponse(code = 400, message = "QuestionList with given TagIds not found.")
    })
    public ResponseEntity<?> findPaginationWithFollowAndWithoutIgnoreTags(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        long userId = securityHelper.getPrincipal().getId();

        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithFollowAndWithoutIgnoreTags(page, size, userId);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/search", params = {"page", "size"})
    @ApiOperation(value = "Return Questions by search value")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class)
    })

    public ResponseEntity<?> qetQuestionBySearch(
            @Valid @RequestBody QuestionSearchDto questionSearchDto,
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true, example = "10")
            @RequestParam("size") int size) {

        if (size <= 0 || page <= 0 || size > MAX_ITEMS_ON_PAGE) {
            ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage =
                questionDtoService.getQuestionBySearchValue(questionSearchDto.getMessage(), page, size);
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("{questionId}/comment")
    @ApiOperation(value = "Add comment", notes = "This method Add comment to question and return CommentDto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Comment was added", response = CommentDto.class),
            @ApiResponse(code = 400, message = "Question or user not found", response = String.class),
            @ApiResponse(code = 400, message = "This user already commented this question", response = String.class)
    })

    public ResponseEntity<?> addCommentToQuestion(
            @ApiParam(name = "QuestionId", value = "QuestionId. Type long", required = true, example = "1")
            @PathVariable Long questionId,

            @ApiParam(name = "text", value = "Text of comment. Type string", required = true, example = "Some comment")
            @RequestBody String commentText) {

        User user = securityHelper.getPrincipal();

        Optional<Question> question = questionService.getById(questionId);
        if (!question.isPresent()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        boolean existsCommentByUser = commentDtoService.isUserAlreadyCommentedQuestion(user.getId(), questionId);
        if (!existsCommentByUser) {
            CommentQuestion commentQuestion =
                    commentQuestionService.addCommentToQuestion(commentText, question.get(), user);

            return ResponseEntity.ok(commentConverter.commentToCommentDTO(commentQuestion));
        }
        return ResponseEntity.badRequest().body("You've already commented this question");
    }

    @GetMapping("/{questionId}/comments")
    @ApiOperation(value = "Return all Comments by questionID", notes = "Return all Comments by questionID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Return all Comments by questionID", response = CommentDto.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Question not found", response = String.class)
    })

    public ResponseEntity<?> getCommentListByQuestionId(@ApiParam(name = "questionId", value = "questionId. Type long", required = true, example = "1")
                                                        @PathVariable Long questionId) {

        Optional<Question> question = questionService.getById(questionId);
        if (!question.isPresent()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        List<CommentQuestionDto> commentQuestionDtoList = commentDtoService.getAllCommentsByQuestionId(questionId);

        return ResponseEntity.ok(commentQuestionDtoList);
    }

    @PostMapping("/{questionId}/upVote")
    @ResponseBody
    @ApiOperation(value ="Up vote for question")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Question was up voted", response = VoteQuestionDto.class),
            @ApiResponse(code = 400, message = "Question not found", response = String.class)
    })

    public ResponseEntity<?> questionUpVote(
            @ApiParam(name = "questionId", value = "type Long", required = true, example = "0")
            @PathVariable Long questionId) {

        Optional<Question> questionOptional = questionService.getById(questionId);
        if (!questionOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Question was not found");
        }

        Question question = questionOptional.get();
        User user = securityHelper.getPrincipal();

        VoteQuestion voteQuestion;
        try {
            voteQuestion = voteQuestionService.questionUpVote(question, user);
        }  catch (VoteException e) {
            return ResponseEntity.ok(e.getMessage());
        }
        VoteQuestionDto responseBody = voteQuestionConverter.voteQuestionToVoteQuestionDto(voteQuestion);

        reputationService.increaseReputationByQuestionVoteUp(question, user);

        return ResponseEntity.ok(responseBody);


    }



    @PostMapping("/{questionId}/downVote")
    @ResponseBody
    @ApiOperation(value ="Down vote for question")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Question was up voted", response = VoteQuestionDto.class),
            @ApiResponse(code = 400, message = "Question not found", response = String.class)
    })
    public ResponseEntity<?> questionDownVote(
            @ApiParam(name = "questionId", value = "type Long", required = true, example = "0")
            @PathVariable Long questionId) {

        Optional<Question> questionOptional = questionService.getById(questionId);
        if (!questionOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Question was not found");
        }

        Question question = questionOptional.get();
        User user = securityHelper.getPrincipal();

        VoteQuestion voteQuestion;
        try {
            voteQuestion = voteQuestionService.questionDownVote(question, user);
        }  catch (VoteException e) {
            return ResponseEntity.ok(e.getMessage());
        }
        VoteQuestionDto responseBody = voteQuestionConverter.voteQuestionToVoteQuestionDto(voteQuestion);

        reputationService.decreaseReputationByQuestionVoteDown(question, user);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping(value = "/withoutAnswer/new", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers sorted by new")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswerNew(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswersNew(page, size);
        return ResponseEntity.ok(resultPage);
    }


    @PostMapping("{questionId}/view")
    @ApiOperation(value = "Mark as viewed", notes = "Аdd notes at the table 'question_viewed' ")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Question was marked as viewed", response = QuestionDto.class),
            @ApiResponse(code = 400, message = "Question or user not found", response = String.class)
    })
    public ResponseEntity<?> markQuestionAsViewed(

            @ApiParam(name = "QuestionId", value = "QuestionId. Type long", required = true, example = "1")
            @PathVariable Long questionId) {

        User user = securityHelper.getPrincipal();

        if (questionId == null) {
            return ResponseEntity.badRequest().body("Question id is null");
        }

        Optional<Question> question = questionService.getById(questionId);
        if (!question.isPresent()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
        questionViewedService.markQuestionAsViewed(question.get(), user);

        return ResponseEntity.ok().body("Question was marked");
    }


    @GetMapping(value = "/withoutAnswer/trackedTag", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers sorted by trackedTag")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswerTrackedTag(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswersTrackedTag(page, size, securityHelper.getPrincipal().getId() );
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping(value = "/withoutAnswer/ignoredTag", params = {"page", "size"})
    @ApiOperation(value = "Return Questions without answers sorted by trackedTag")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDto>"),
    })
    public ResponseEntity<?> getQuestionsWithoutAnswerIgnoredTag(
            @ApiParam(name = "page", value = "Number Page. type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE,
                    required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<QuestionDto, Object> resultPage = questionDtoService.getPaginationWithoutAnswersIgnoredTags(page, size, securityHelper.getPrincipal().getId() );
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/vote/{questionId}")
    @ApiOperation(value = "Returns value of user's vote on question")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns user's vote on question"),
            @ApiResponse(code = 400, message = "User or question not found")
    })
    public ResponseEntity<?> getUserVoteOnQuestion(
            @ApiParam(name = "questionId", value = "QuestionId, type Long", required = true, example = "1")
            @PathVariable("questionId") Long questionId) {
        Optional<Question> question = questionService.getById(questionId);

        if (!question.isPresent()) {
            return ResponseEntity.badRequest().body("Question was not found");
        }
        Long userId;
        int vote;
        if(voteQuestionService.isUserAlreadyVoted(questionService.getById(questionId).get(), securityHelper.getPrincipal()) == true) {
            userId = securityHelper.getPrincipal().getId();
            vote = voteQuestionDtoDao.getVoteByQuestionIdAndUserId(questionId, userId).get().getVote();
        } else {
            vote = 0;
        }
        return ResponseEntity.ok(vote);
    }
}