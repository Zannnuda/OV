package com.javamentor.qa.platform.webapp.controllers;

import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.QuestionUpdateDto;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.security.util.SecurityHelper;
import com.javamentor.qa.platform.service.abstracts.model.AnswerService;
import com.javamentor.qa.platform.service.abstracts.model.QuestionService;
import com.javamentor.qa.platform.webapp.converters.QuestionConverter;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/moder/")
@Api(value = "ModerApi")
public class ModerController {

    private final QuestionConverter questionConverter;
    private final QuestionService questionService;
    private final SecurityHelper securityHelper;
    private final AnswerService answerService;

    @Autowired
    public ModerController(QuestionConverter questionConverter,
                           QuestionService questionService, AnswerService answerService, SecurityHelper securityHelper) {
        this.questionConverter = questionConverter;
        this.questionService = questionService;
        this.answerService = answerService;
        this.securityHelper = securityHelper;
    }

    @PutMapping("/question/{id}")
    @ApiOperation(value = "Save the modified question", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the QuestionDto", response = QuestionDto.class),
            @ApiResponse(code = 400, message = "Changes are not saved", response = String.class)
    })
    public ResponseEntity<?> updateQuestionById(
            @Valid @RequestBody QuestionUpdateDto questionUpdateDto,
            @ApiParam(name = "id", value = "question id, type Long", required = true, example = "0")
            @PathVariable Long id) {

        Optional<Question> optionalQuestionFromDB = questionService.getById(id);

        if ( !optionalQuestionFromDB.isPresent() ) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        Question updatedQuestion = questionConverter
                .questionUpdateDtoToQuestion(optionalQuestionFromDB.get(), questionUpdateDto);

        questionService.update(updatedQuestion);

        return ResponseEntity.ok("Question updated");
    }

    @PatchMapping("/{answerId}/delete")
    @ApiOperation(value = "Delete answer by moderator", notes = "This method deletes answer to question")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Answer was deleted", response = String.class),
            @ApiResponse(code = 400, message = "Answer not found", response = String.class)
    })
    @Secured({ "ROLE_ADMIN", "ROLE_MODER" })
    public ResponseEntity<?> deleteAnswerFromQuestion(@ApiParam(name = "answerId", value = "answerId. Type long", required = true, example = "1")
                                                      @PathVariable Long answerId) {
        Optional<Answer> answer = answerService.getById(answerId);
        if (!answer.isPresent()) {
            return ResponseEntity.badRequest().body("Answer was not found");
        }
        answerService.markAnswerAsDeleted(answerId);
        return ResponseEntity.ok("Answer was deleted by moderator");
    }
}
