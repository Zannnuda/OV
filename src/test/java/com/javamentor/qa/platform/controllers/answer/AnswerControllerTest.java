package com.javamentor.qa.platform.controllers.answer;

import com.github.database.rider.core.api.dataset.DataSet;
import com.javamentor.qa.platform.AbstractIntegrationTest;
import com.javamentor.qa.platform.models.dto.CreateAnswerDto;
import com.javamentor.qa.platform.models.dto.AnswerDto;
import com.javamentor.qa.platform.models.dto.VoteAnswerDto;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.question.answer.CommentAnswer;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.webapp.converters.AnswerConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DataSet(value = {"" +
        "dataset/answer/usersApi.yml",
        "dataset/answer/answerApi.yml",
        "dataset/answer/roleApi.yml",
        "dataset/question/questionQuestionApi.yml",
        "dataset/answer/questionApi.yml",
        "dataset/answer/votes_on_answers.yml"},
        cleanBefore = true)
@WithMockUser(username = "principal@mail.ru", roles = {"ADMIN", "USER"})
@ActiveProfiles("local")
class AnswerControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnswerConverter answerConverter;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldAddCommentToAnswerResponseBadRequestAnswerNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/10/answer/99999/comment")
                .content("This is very good answer!")
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Answer not found"));
    }


    @Test
    void shouldAddCommentToAnswerResponseCommentDto() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/14/answer/20/comment")
                .content("This is very good answer!")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("This is very good answer!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persistDate", org.hamcrest.Matchers.containsString(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastRedactionDate", org.hamcrest.Matchers.containsString(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.commentType").value("ANSWER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Teat"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reputation").value(2))
                .andReturn();

        JSONObject dto = new JSONObject(result.getResponse().getContentAsString());

        List<CommentAnswer> resultList = entityManager.createNativeQuery("select * from comment_answer where comment_id = " + dto.get("id")).getResultList();
        Assertions.assertFalse(resultList.isEmpty());
    }

    @Test
    void shouldAddAnswerToQuestionStatusOk() throws Exception {

        CreateAnswerDto createAnswerDto = new CreateAnswerDto();
        createAnswerDto.setHtmlBody("test answer");

        String jsonRequest = objectMapper.writeValueAsString(createAnswerDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/14/answer")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddAnswerToQuestionResponseStatusOk() throws Exception {
        CreateAnswerDto createAnswerDto = new CreateAnswerDto();
        createAnswerDto.setHtmlBody("test answer");

        String jsonRequest = objectMapper.writeValueAsString(createAnswerDto);

        String resultContext = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/15/answer")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(createAnswerDto.getHtmlBody()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.questionId").value(15))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
                .andReturn().getResponse().getContentAsString();

        AnswerDto answerDtoFromResponse = objectMapper.readValue(resultContext, AnswerDto.class);
        Answer answer = entityManager
                .createQuery("From Answer where id = :id", Answer.class)
                .setParameter("id", answerDtoFromResponse.getId())
                .getSingleResult();
        AnswerDto answerDtoFromDB = answerConverter.answerToAnswerDTO(answer);

        Assertions.assertEquals(answerDtoFromResponse.getBody(), answerDtoFromDB.getBody());
    }

    @Test
    void shouldAddAnswerToQuestionResponseBadRequestQuestionNotFound() throws Exception {

        CreateAnswerDto createAnswerDto = new CreateAnswerDto();
        createAnswerDto.setHtmlBody("test answer");

        String jsonRequest = objectMapper.writeValueAsString(createAnswerDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/2222/answer")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Question was not found"));
    }

    @Test
    void voteUpStatusOk() throws Exception {

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/upVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Correct vote"));

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers").getResultList();
        int second = after.size();
        Assertions.assertEquals(first + 1, second);
    }

    @Test
    void voteUpQuestionIsNotExist() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/100/answer/13/upVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Question was not found"));

    }

    @Test
    void voteUpAnswerIsNotExist() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/1/answer/4/upVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Answer was not found"));
    }

    @Test
    void voteDownStatusOk() throws Exception {

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/downVote")
                .contentType("text/plain;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers").getResultList();
        int second = after.size();
        Assertions.assertEquals(first + 1, second);
    }
    //тестирую возможность двойного голоса на ответ в одном вопросе
    @Test
    void voteUpInQuestionOneVoteStatusOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/1/answer/14/upVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Can't change vote"));

    }
    //тестирую возможность двойного голоса на ответ в одном вопросе
    @Test
    void voteDownInQuestionOneVoteStatusOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/1/answer/51/downVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User already voted in this question"));

    }

    @Test
    void voteDownQuestionIsNotExist() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/100/answer/14/downVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Question was not found"));

    }

    @Test
    void voteDownAnswerIsNotExist() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/1/answer/4/downVote")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Answer was not found"));
    }


    @Test
    void userVotedForAnswerStatusOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/question/1/isAnswerVoted"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("true"));
    }

    @Test
    void userNotVotedForAnswerStatusOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/question/2/isAnswerVoted"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("false"));
    }

    @Test
    void userNotVotedForAnswerCuzQuestionNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/question/0/isAnswerVoted"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Question was not found"));
    }


    @Test
    void shouldGetAllCommentsByAnswer() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/9/answer/30/comment")
                .content("This is very good answer!")
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/9/answer/20/comment")
                .content("Hi! I know better than you :-) !")
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/9/answer/30/comment")
                .content("The bad answer!")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/question/9/answer/30/comments")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        JSONArray array = new JSONArray(result.getResponse().getContentAsString());

        Assertions.assertEquals(1, array.length());
    }

    @Test
    @WithMockUser(username = "admin@tut.by", roles = {"ADMIN"})
    void shouldMarkAnswerAsHelpful() throws Exception {

        Answer beforeAnswer = (Answer) entityManager.createQuery("from Answer Where id=40").getSingleResult();

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/77/answer/40/upVote"))
                .andReturn();

        Answer afterAnswer = (Answer) entityManager.createQuery("from Answer Where id=40").getSingleResult();

        Assertions.assertFalse(beforeAnswer.getIsHelpful());
        Assertions.assertTrue(afterAnswer.getIsHelpful());
    }

    @Test
    void shouldAddSecondAnswerToQuestionStatusBadRequest() throws Exception {

        CreateAnswerDto createAnswerDto = new CreateAnswerDto();
        createAnswerDto.setHtmlBody("test answer");

        String jsonRequest = objectMapper.writeValueAsString(createAnswerDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/14/answer")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/14/answer")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    public void shouldAddSecondCommentToAnswerResponseBadRequest() throws Exception {
        JSONObject commentFirst = new JSONObject("{\"text\":\"This is the first comment to answer!\"}");
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/10/answer/51/comment")
                .content(commentFirst.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        JSONObject commentSecond = new JSONObject("{\"text\":\"This is the second comment to answer from the same user!\"}");
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/question/10/answer/51/comment")
                .content(commentSecond.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You have already commented this answer"));

    }

    @Test
    void answerVoteUpAndVoteUpAgainStatusOk() throws Exception {

        VoteAnswerDto voteAnswerDto = new VoteAnswerDto();
        voteAnswerDto.setUserId(1L);
        voteAnswerDto.setAnswerId(51L);
        voteAnswerDto.setPersistDateTime(LocalDateTime.now());
        voteAnswerDto.setVote(1);

        String jsonRequestVote = objectMapper.writeValueAsString(voteAnswerDto);

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/upVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/upVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int second = after.size();
        Assertions.assertEquals(first, second);
    }

    @Test
    void answerVoteDownAndVoteDownAgainStatusOk() throws Exception {

        VoteAnswerDto voteAnswerDto = new VoteAnswerDto();
        voteAnswerDto.setUserId(1L);
        voteAnswerDto.setAnswerId(51L);
        voteAnswerDto.setPersistDateTime(LocalDateTime.now());
        voteAnswerDto.setVote(-1);

        String jsonRequest = objectMapper.writeValueAsString(voteAnswerDto);

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/downVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/downVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk());

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int second = after.size();
        Assertions.assertEquals(first, second);
    }

    @Test
    void answerVoteUpAndVoteDownStatusOk() throws Exception {

        VoteAnswerDto voteAnswerDto = new VoteAnswerDto();
        voteAnswerDto.setUserId(1L);
        voteAnswerDto.setAnswerId(51L);
        voteAnswerDto.setPersistDateTime(LocalDateTime.now());
        voteAnswerDto.setVote(1);

        String jsonRequestVote = objectMapper.writeValueAsString(voteAnswerDto);

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/upVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        voteAnswerDto.setVote(-1);
        jsonRequestVote = objectMapper.writeValueAsString(voteAnswerDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/downVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int second = after.size();
        Assertions.assertEquals(first + 1, second);
    }

    @Test
    void answerVoteDownAndVoteUpStatusOk() throws Exception {

        VoteAnswerDto voteAnswerDto = new VoteAnswerDto();
        voteAnswerDto.setUserId(1L);
        voteAnswerDto.setAnswerId(51L);
        voteAnswerDto.setPersistDateTime(LocalDateTime.now());
        voteAnswerDto.setVote(-1);

        String jsonRequestVote = objectMapper.writeValueAsString(voteAnswerDto);

        List<VoteAnswer> before = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int first = before.size();

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/downVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        voteAnswerDto.setVote(1);
        jsonRequestVote = objectMapper.writeValueAsString(voteAnswerDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/question/10/answer/51/upVote")
                .contentType("application/json;charset=UTF-8")
                .content(jsonRequestVote))
                .andDo(print())
                .andExpect(status().isOk());

        List<VoteAnswer> after = entityManager.createNativeQuery("select * from votes_on_answers where answer_id = 51").getResultList();
        int second = after.size();
        Assertions.assertEquals(first + 1, second);
    }

    @Test
    public void shouldAddPositiveReputationByAnswerVoteUp() throws Exception {
        try {
            String string = "FROM Reputation WHERE answer.id =: answerId AND sender.id =: senderId";

            Query queryBefore = entityManager.createQuery(string, Reputation.class);
            queryBefore.setParameter("answerId", 51L);
            queryBefore.setParameter("senderId", 153L);

            Reputation reputationBefore = (Reputation) queryBefore.getSingleResult();

            MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/question/10/answer/51/upVote")).andReturn();

            Query queryAfter = entityManager.createQuery(string, Reputation.class);
            queryAfter.setParameter("answerId", 51L);
            queryAfter.setParameter("senderId", 153L);

            Reputation reputationAfter = (Reputation) queryAfter.getSingleResult();

            int votePoints = reputationAfter.getCount();
            long lastSenderId = reputationAfter.getId();

            Assert.assertTrue(reputationBefore != reputationAfter);
            Assert.assertEquals(lastSenderId, 153L);
            Assert.assertEquals(votePoints, 20);

        } catch (NoResultException ignore) {

        }
    }

    @Test
    public void shouldAddNegativeReputationByAnswerVoteDown() throws Exception {
        try {
            String string = "FROM Reputation WHERE answer.id =: answerId AND sender.id =: senderId";

            Query queryBefore = entityManager.createQuery(string, Reputation.class);
            queryBefore.setParameter("answerId", 51L);
            queryBefore.setParameter("senderId", 153L);

            Reputation reputationBefore = (Reputation) queryBefore.getSingleResult();

            MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/question/10/answer/51/upVote")).andReturn();

            Query queryAfter = entityManager.createQuery(string, Reputation.class);
            queryAfter.setParameter("answerId", 51L);
            queryAfter.setParameter("senderId", 153L);

            Reputation reputationAfter = (Reputation) queryAfter.getSingleResult();

            int votePoints = reputationAfter.getCount();
            long lastSenderId = reputationAfter.getId();

            Assert.assertTrue(reputationBefore != reputationAfter);
            Assert.assertEquals(lastSenderId, 153L);
            Assert.assertEquals(votePoints, -20);

        } catch (NoResultException ignore) {

        }
    }
}