package com.javamentor.qa.platform.webapp.controllers;

import com.javamentor.qa.platform.models.dto.*;
import com.javamentor.qa.platform.models.entity.BookMarks;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.util.OnCreate;
import com.javamentor.qa.platform.models.util.OnUpdate;
import com.javamentor.qa.platform.security.util.SecurityHelper;
import com.javamentor.qa.platform.service.abstracts.dto.*;
import com.javamentor.qa.platform.service.abstracts.model.BookMarksService;
import com.javamentor.qa.platform.service.abstracts.model.QuestionService;
import com.javamentor.qa.platform.service.abstracts.model.ReputationService;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import com.javamentor.qa.platform.webapp.converters.UserConverter;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/user/")
@Api(value = "UserApi")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final UserDtoService userDtoService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityHelper securityHelper;
    private final AnswerDtoService answerDtoService;
    private final QuestionDtoService questionDtoService;

    private static final int MAX_ITEMS_ON_PAGE = 100;

    private final ReputationService reputationService;
    private final ReputationDtoService reputationDtoService;

    private final BookmarkDtoService bookmarkDtoService;
    private final BookMarksService bookMarksService;

    private final QuestionService questionService;

    private final TopUsersByTagDtoService topUsersByTagDtoService;

    @Autowired
    public UserController(UserService userService,
                          UserConverter userConverter,
                          UserDtoService userDtoService,
                          PasswordEncoder passwordEncoder,
                          SecurityHelper securityHelper,
                          ReputationService reputationService,
                          ReputationDtoService reputationDtoService,
                          AnswerDtoService answerDtoService,
                          QuestionDtoService questionDtoService,
                          BookmarkDtoService bookmarkDtoService,
                          TopUsersByTagDtoService topUsersByTagDtoService,
                          BookMarksService bookMarksService,
                          QuestionService questionService) {

        this.userService = userService;
        this.userConverter = userConverter;
        this.userDtoService = userDtoService;
        this.passwordEncoder = passwordEncoder;
        this.securityHelper = securityHelper;
        this.reputationService = reputationService;
        this.reputationDtoService = reputationDtoService;
        this.answerDtoService = answerDtoService;
        this.questionDtoService = questionDtoService;
        this.bookMarksService = bookMarksService;
        this.questionService = questionService;
        this.bookmarkDtoService = bookmarkDtoService;
        this.topUsersByTagDtoService = topUsersByTagDtoService;
    }

    // Examples for Swagger
    @GetMapping("{id}")
    @ApiOperation(value = "Return message(Object)", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the object.", response = UserDto.class),
            @ApiResponse(code = 400, message = "Wrong ID",response = String.class)
    })
    public  ResponseEntity<?> getUserById(
        @ApiParam(name="id",value="type Long(or other described)", required = true, example="0")
        @PathVariable Long id){
        Optional<UserDto> userDto = userDtoService.getUserDtoById(id);
        return userDto.isPresent() ? ResponseEntity.ok().body(userDto.get()):
                ResponseEntity.badRequest()
                        .body("User with id " + id + " not found");

   }

    @GetMapping("order/reputation/week")
    @ApiOperation(value = "Get page List<UserDtoList> order by reputation over week." +
            "UserDtoList contains List<TagDto> with size 3 " +
            "and order by activity. Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<UserDtoList> order by reputation over week",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong value of size or page", response = String.class)
    })
    public ResponseEntity<?> getUserDtoListPaginationByReputationOverWeek(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of records per page.Type int." +
                    "Maximum number of records per page"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<UserDtoList,Object> resultPage = userDtoService.getPageUserDtoListByReputationOverWeek(page, size);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("order/reputation")
    @ApiOperation(value = "Get page List<UserDtoList> order by reputation. " +
            "UserDtoList contains List<TagDto> with size 3 " +
            "and order by activity. Max size entries on page = "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<UserDtoList> order by reputation",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong value of size or page", response = String.class)
    })
    public ResponseEntity<?> getUserDtoListPaginationByReputation(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "1")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of records per page. Type int. " +
                    "Maximum number of records per page "+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {

        if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<UserDtoList,Object> resultPage = userDtoService.getPageUserDtoListByReputation(page, size);

        return  ResponseEntity.ok(resultPage);
    }

    // вывод юзеров с репутацией за месяц
    @GetMapping("order/reputation/month")
    @ApiOperation(value = "Get page List<UserDtoList> order by reputation over month." +
            "UserDtoList contains List<TagDto> with size 3 " +
            "and order by activity. Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<UserDtoList> order by reputation over month",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong ID", response = String.class)
    })
    public ResponseEntity<?> getUserDtoPaginationByReputationOverMonth(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<UserDtoList, Object> resultPage = userDtoService.getPageUserDtoListByReputationOverMonth(page,size);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("order/reputation/year")
    @ApiOperation(value = "Get page List<UserDtoList> order by reputation over year." +
            "UserDtoList contains List<TagDto> with size 3 " +
            "and order by activity. Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<UserDtoList> order by reputation over year",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong ID", response = String.class)
    })
    public ResponseEntity<?> getUserDtoPaginationByReputationOverYear(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<UserDtoList, Object> resultPage = userDtoService.getPageUserDtoListByReputationOverYear(page,size);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("find")
    @ApiOperation(value = "Return page List<UserDtoList>", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the object.", response = List.class),
            @ApiResponse(code = 400, message = "User with this name does not exist", response = String.class)
    })
    public ResponseEntity<?> getUserListByName(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int. " +
                    "Maximum number of records per page " + MAX_ITEMS_ON_PAGE, required = true,
                    example = "10")
            @RequestParam("size") int size,
            @RequestParam("name") String name) {

        if (userService.getUserByName(name).isPresent()) {
            if (page <= 0 || size <= 0 || size > MAX_ITEMS_ON_PAGE) {
                return ResponseEntity.badRequest().body("The page number and size must be positive. " +
                        "Maximum number of records per page " + MAX_ITEMS_ON_PAGE);
            } else {
                PageDto<UserDtoList, Object> resultPage = userDtoService.getPageUserDtoListByName(page, size, name);
                return ResponseEntity.ok(resultPage);
            }
        } else {
            return ResponseEntity.badRequest().body("User with this name does not exist");
        }
    }


    @GetMapping("public-info")
    @ApiOperation(value = "Get user public info", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User public info", response = String.class),
            @ApiResponse(code = 400, message = "User not found", response = String.class)
    })
    @Validated(OnUpdate.class)
    public ResponseEntity<?> getInfo() {

        Optional<User> optionalUser = userService.getUserById(securityHelper.getPrincipal().getId());

        if(!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        UserPublicInfoDto userPublicInfoDto = userConverter.userToUserPublicInfoDto(optionalUser.get());

        return ResponseEntity.ok(userPublicInfoDto);
    }


    @PostMapping("public-info")
    @ApiOperation(value = "Update user public info", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User public info updated successfully", response = String.class),
            @ApiResponse(code = 400, message = "Something goes wrong", response = String.class)
    })
    public ResponseEntity<?> updateUserDtoPublicInfo(@RequestBody UserPublicInfoDto userPublicInfoDto) {

        User user = userConverter.userPublicInfoDtoToUser(userPublicInfoDto);
        user.setId(securityHelper.getPrincipal().getId());
        userService.updateUserPublicInfo(user);

        return ResponseEntity.ok("User public info updated successfully");
    }


    @PostMapping("password/reset")
    @ApiOperation(value = "Reset user password", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "password reset successfully", response = String.class),
            @ApiResponse(code = 400, message = "Something goes wrong",response = String.class)
    })
    @Validated(OnUpdate.class)
    public ResponseEntity<?> resetPassword (@Valid @RequestBody UserResetPasswordDto userResetPasswordDto) {

        User user = securityHelper.getPrincipal();

        if (!passwordEncoder.matches(userResetPasswordDto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(userResetPasswordDto.getNewPassword()));
        userService.resetPassword(user);

        return ResponseEntity.ok().body("Password reset successfully");
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "Set up user's field isDeleted to true", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User deleted successfully", response = String.class),
            @ApiResponse(code = 400, message = "Something goes wrong",response = String.class),
            @ApiResponse(code = 400, message = "The user has already been deleted!",response = String.class)
    })
    public ResponseEntity<?> deleteUser() {

        User user = securityHelper.getPrincipal();
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            return ResponseEntity.badRequest().body("The user has already been deleted!");
        }
        userService.deleteUserByFlag(user); // TODO: find out, if reputation needs to be cleared/deleted, as user get deleted
        return ResponseEntity.ok().body("User deleted successfully");

    }

    @PostMapping("persistWithoutConfirm")
    @ApiOperation(value = "User creation",
            notes = "Provide valid UserRegistrationDto object, to persist user, WITHOUT email confirmation",
            response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User persisted successful", response = UserDto.class),
            @ApiResponse(code = 400, message = "Persist failed", response = String.class)
    })
    @Validated(OnCreate.class)
    public ResponseEntity<?> createUserWithoutConfirm(@Valid @RequestBody UserRegistrationDto userRegistrationDto ) {

        String email = userRegistrationDto.getEmail();
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isPresent()) {

            return ResponseEntity.badRequest().body(String.format("User with email %s already exist", email));

        }

        User newUser = userConverter.userDtoToUser(userRegistrationDto);
        userService.persist(newUser);

        return ResponseEntity.ok(userConverter.userToDto(newUser));
    }

    @GetMapping("reputation/history/{id}")
    @ApiOperation(value = "Get page List<ReputationHistoryDtoList>. " +
            "Max size entries on page= " + MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<ReputationHistoryDtoList>",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong value of size, page or id", response = String.class)
    })
    public ResponseEntity<?> getReputationHistoryDtoListPagination(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of records per page.Type int." +
                    "Maximum number of records per page"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size,
            @ApiParam(name="id",value="type Long(or other described)", required = true, example="0")
            @PathVariable Long id) {
        if (page <= 0 || size <= 0 || id <= 0 || size > MAX_ITEMS_ON_PAGE) {
            return ResponseEntity.badRequest().body("Номер страницы, id и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }
        PageDto<ReputationHistoryDtoList, Object> resultPage = reputationDtoService.getPageReputationHistoryDto(page, size, id);

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("currentUser/answers")
    @ApiOperation(value = "Get page List<AnswerDto> principal user order by votes." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = AnswerDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<AnswerDto> principal user order by votes",
                    response = List.class),
            @ApiResponse(code = 400, message = "Request wrong", response = String.class)
    })
    public ResponseEntity<?> getAnswerListPrincipalUser(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        User user = securityHelper.getPrincipal();

        PageDto<AnswerDto, Object> resultPage = answerDtoService.getAllAnswersOfPrincipalUserOrderByVotes(page,size, user.getId());

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("currentUser/questions")
    @ApiOperation(value = "Get page List<QuestionDtoPrincipal> principal user order by persist." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = AnswerDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDtoPrincipal> principal user order by persist",
                    response = List.class),
            @ApiResponse(code = 400, message = "Request wrong", response = String.class)
    })
    public ResponseEntity<?> getQuestionListPrincipalUser(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        User user = securityHelper.getPrincipal();

        PageDto<QuestionDtoPrincipal, Object> resultPage = questionDtoService.getAllQuestionsOfPrincipalUserOrderByPersist(page, size, user.getId());

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("order/questions/votes/{userId}")
    @ApiOperation(value = "Get page List<QuestionsDto> order by votes." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<UserDtoList> order by reputation over month",
                    response = List.class),
            @ApiResponse(code = 400, message = "Wrong ID", response = String.class)
    })
    public ResponseEntity<?> getUserQuestionsOrderedByVotesPagination(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size,
            @PathVariable("userId") long id) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDto, Object> resultPage = userDtoService.getUserQuestionsSortedByVotes(page,size, id);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("tag/{tagId}/topUsers")
    @ApiOperation(value = "Get page List<UserDto> order by tagId." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request success",
                    response = List.class)
    })
    public ResponseEntity<?> getTopUsersByTagId(
            @PathVariable("tagId") long id) {

        Optional<UserDto> resultTopUser = topUsersByTagDtoService.getTopUsersDtoByTagId(id);

        return  ResponseEntity.ok(resultTopUser);
    }

    @GetMapping("bookmarks")
    @ApiOperation(value = "Return message(Object)", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request success", response = BookmarkDto.class),
            @ApiResponse(code = 400, message = "Something is wrong", response = String.class)
    })
    public ResponseEntity<?> getBookmarksByUserId() {

        User user = securityHelper.getPrincipal();

        List<BookmarkDto> resultBookmark = bookmarkDtoService.getBookmarkDtoByUserId(user.getId());

        return  !resultBookmark.isEmpty()
                    ? ResponseEntity.ok().body(resultBookmark)
                    : ResponseEntity.badRequest().body("Bad request");
    }

//    @GetMapping("bookmarks/{questionId}")
//    @ApiOperation(value = "Return message(string)", response = String.class)
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "The bookmark exists", response = String.class),
//            @ApiResponse(code = 400, message = "There is no such bookmark", response = String.class)
//    })
//    public ResponseEntity<?> getBookmarkByQuestionId(
//            @ApiParam(name = "questionId", value = "Question id. Type long", required = true, example = "10")
//            @PathVariable("questionId") long questionId) {
//
//        User user = securityHelper.getPrincipal();
//        Optional<BookMarks> bookmarkByQuestionId = bookMarksService.getBookmarkByQuestionId(questionId, user.getId());
//
//        if (!bookmarkByQuestionId.isPresent()) {
//            return ResponseEntity.badRequest().body("There is no such bookmark");
//        }
//
//        return  ResponseEntity.ok().body("The bookmark exists");
//    }

    @PostMapping("bookmarks/{questionId}")
    @ApiOperation(value = "Return message(String)", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Bookmark added successfully", response = String.class),
            @ApiResponse(code = 400, message = "There is no such question", response = String.class)
    })
    public ResponseEntity<?> addBookmark(
            @ApiParam(name = "questionId", value = "Question id. Type long", required = true, example = "10")
            @PathVariable("questionId") long questionId) {

        Optional<Question> optionalQuestion = questionService.getById(questionId);
        if (!optionalQuestion.isPresent()) {
            return ResponseEntity.badRequest().body("There is no such question");
        }
        User user = securityHelper.getPrincipal();

        BookMarks bookMarks = new BookMarks();
        bookMarks.setQuestion(optionalQuestion.get());
        bookMarks.setUser(user);

        bookMarksService.persist(bookMarks);

        return  ResponseEntity.ok().body("Bookmark added successfully");
    }

    @DeleteMapping("bookmarks/{questionId}")
    @ApiOperation(value = "Return message(string)", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "The bookmark deleted", response = String.class),
            @ApiResponse(code = 400, message = "There is no such bookmark", response = String.class)
    })
    public ResponseEntity<?> removeBookmark(
            @ApiParam(name = "questionId", value = "Question id. Type long", required = true, example = "10")
            @PathVariable("questionId") long questionId) {

        User user = securityHelper.getPrincipal();
        Optional<BookMarks> bookmarkByQuestionId = bookMarksService.getBookmarkByQuestionId(questionId, user.getId());

        if (!bookmarkByQuestionId.isPresent()) {
            return ResponseEntity.badRequest().body("There is no such question");
        }

        bookMarksService.delete(bookmarkByQuestionId.get());

        return  ResponseEntity.ok().body("The bookmark deleted");
    }

    @GetMapping("/userProfile/{id}")
    @ApiOperation(value = "Get user or principal", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the object.", response = UserDto.class),
            @ApiResponse(code = 400, message = "Wrong ID",response = String.class)
    })
    public  ResponseEntity<?> getUserProfileById(
            @ApiParam(name="id",value="type Long(or other described), if id = 0, return principal user", required = true, example="0")
            @PathVariable Long id){
        if(id == 0){
            id = securityHelper.getPrincipal().getId();
        }
        Optional<UserDto> userDto = userDtoService.getUserDtoById(id);
        return userDto.isPresent() ? ResponseEntity.ok().body(userDto.get()):
                ResponseEntity.badRequest()
                        .body("User with id " + id + " not found");
    }

    @GetMapping("userAnswers/{id}")
    @ApiOperation(value = "Get page List<AnswerDto> User order by votes." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = AnswerDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<AnswerDto> principal user order by votes",
                    response = List.class),
            @ApiResponse(code = 400, message = "Request wrong", response = String.class)
    })
    public ResponseEntity<?> getAnswerListUserById(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size,
            @ApiParam(name="id",value="type Long(or other described)", required = true, example="0")
            @PathVariable Long id) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<AnswerDto, Object> resultPage = answerDtoService.getAllAnswersOfPrincipalUserOrderByVotes(page,size, id);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("userQuestions/{id}")
    @ApiOperation(value = "Get page List<QuestionDtoPrincipal> user order by persist." +
            "Max size entries on page= "+ MAX_ITEMS_ON_PAGE, response = AnswerDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the pagination List<QuestionDtoPrincipal> principal user order by persist",
                    response = List.class),
            @ApiResponse(code = 400, message = "Request wrong", response = String.class)
    })
    public ResponseEntity<?> getQuestionListUserById(
            @ApiParam(name = "page", value = "Number Page. Type int", required = true, example = "10")
            @RequestParam("page") int page,
            @ApiParam(name = "size", value = "Number of entries per page.Type int." +
                    " Максимальное количество записей на странице"+ MAX_ITEMS_ON_PAGE , required = true,
                    example = "10")
            @RequestParam("size") int size,
            @ApiParam(name="id",value="type Long(or other described)", required = true, example="0")
            @PathVariable Long id) {
        if(page <= 0 || size <=0 || size > MAX_ITEMS_ON_PAGE ) {
            return ResponseEntity.badRequest().body("Номер страницы и размер должны быть " +
                    "положительными. Максимальное количество записей на странице " + MAX_ITEMS_ON_PAGE);
        }

        PageDto<QuestionDtoPrincipal, Object> resultPage = questionDtoService.getAllQuestionsOfPrincipalUserOrderByPersist(page, size, id);

        return  ResponseEntity.ok(resultPage);
    }

    @GetMapping("bookmarks/{id}")
    @ApiOperation(value = "Get bookmarks by user id", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request success", response = BookmarkDto.class),
            @ApiResponse(code = 400, message = "Something is wrong", response = String.class)
    })
    public ResponseEntity<?> getBookmarksUserById(
            @ApiParam(name="id",value="type Long(or other described)", required = true, example="0")
            @PathVariable Long id) {

        List<BookmarkDto> resultBookmark = bookmarkDtoService.getBookmarkDtoByUserId(id);

        return  !resultBookmark.isEmpty()
                ? ResponseEntity.ok().body(resultBookmark)
                : ResponseEntity.badRequest().body("Bad request");
    }
}
