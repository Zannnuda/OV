package com.javamentor.qa.platform.controllers.user;

import com.github.database.rider.core.api.dataset.DataSet;
import com.javamentor.qa.platform.AbstractIntegrationTest;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.dto.*;
import com.javamentor.qa.platform.models.entity.chat.GroupChat;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "principal@mail.ru", roles = {"ADMIN", "USER"})
@ActiveProfiles("local")
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String DELETE = "/api/user/delete";
    private static final String BAD_REQUEST_MESSAGE_WRONG = "Something goes wrong";
    private static final String BAD_REQUEST_MESSAGE_ALREADY_DELETED = "The user has already been deleted!";

    @Test
    @DataSet(value = "dataset/user/userApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void shouldGetUserById() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setReputation(2);
        user.setEmail("ivanov@mail.ru");
        user.setFullName("Teat");
        user.setLinkImage("https://www.google.com/search?q=D0");

        this.mockMvc.perform(get("/api/user/" + user.getId()))
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("id").value(user.getId()))
                .andExpect(jsonPath("email").value(user.getEmail()))
                .andExpect(jsonPath("fullName").value(user.getFullName()))
                .andExpect(jsonPath("linkImage").value(user.getLinkImage()))
                .andExpect(jsonPath("reputation").value(user.getReputation()))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "dataset/question/usersQuestionApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void shouldGetUserByIsNot() throws Exception {
        int id = 4;
        this.mockMvc.perform(get("/api/user/" + id))
                .andDo(print())
                .andExpect(content().string("User with id " + id + " not found"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "dataset/user/roleUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void shouldCreateUser() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("11@22.ru");
        user.setPassword("100");
        user.setFullName("Ivan Ivanich");

        String jsonRequest = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/api/auth/reg/registration")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(user.email))
                .andExpect(jsonPath("fullName").value(user.fullName))
                .andExpect(jsonPath("linkImage").isEmpty())
                .andExpect(jsonPath("reputation").value(0))
                .andExpect(status().isOk());

        TypedQuery<User> userQuery = entityManager.createQuery("FROM User WHERE email =: email", User.class)
                .setParameter("email", "11@22.ru");

        Optional<User> newUser = SingleResultUtil.getSingleResultOrNull(userQuery);
        Assertions.assertNotNull(newUser);
        TypedQuery<Reputation> reputationQuery = entityManager.createQuery("FROM Reputation WHERE user.id =: userId", Reputation.class)
                .setParameter("userId", newUser.get().getId());
        Assertions.assertNotNull(SingleResultUtil.getSingleResultOrNull(reputationQuery));
    }

    @Test
    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void shouldNotCreateUser() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("ivanov@mail.ru");
        user.setPassword("100");
        user.setFullName("Ivan Ivanich");
        String jsonRequest = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/auth/reg/registration")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.format("User with email %s already exist", user.getEmail())))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldCreateUserValidateEmail() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("ivanovmail.ru");
        user.setPassword("100");
        user.setFullName("Ivan Ivanich");
        String jsonRequest = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/auth/reg/registration").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("createUser.userRegistrationDto.email: Заданный email не может существовать"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void shouldGetUserByName() throws Exception {

        PageDto<UserDtoList, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(3);
        expected.setItemsOnPage(10);
//---------------------------------------------------------------


        List<UserDtoList> expectedItems = new ArrayList<>();
        expectedItems.add(new UserDtoList(1L, "Teat", "linkImage1", 2, Arrays.asList(new TagDto[]{})));
        expectedItems.add(new UserDtoList(2L, "Teat", "linkImage2", 1, Arrays.asList(new TagDto[]{})));
        expectedItems.add(new UserDtoList(4L, "Tob", "linkImage4", 4, Arrays.asList(new TagDto[]{})));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get("/api/user/find")
                .param("name", "t")
                .param("page", "1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.itemsOnPage").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        PageDto<UserDtoList, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void inabilityGetUserByNameWithWrongName() throws Exception {
        this.mockMvc.perform(get("/api/user/find")
                .param("name", "c")
                .param("page", "1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("User with this name does not exist"));
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void inabilityGetUserByNameWithNegativePage() throws Exception {
        this.mockMvc.perform(get("/api/user/find")
                .param("name", "t")
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("The page number and size must be positive. Maximum number of records per page 100"));
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void inabilityGetUserByNameWithZeroSize() throws Exception {
        this.mockMvc.perform(get("/api/user/find")
                .param("name", "t")
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("The page number and size must be positive. Maximum number of records per page 100"));
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void inabilityGetUserByNameWithNegativeSize() throws Exception {
        this.mockMvc.perform(get("/api/user/find")
                .param("name", "t")
                .param("page", "1")
                .param("size", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("The page number and size must be positive. Maximum number of records per page 100"));
    }

    @Test
    @DataSet(value = "dataset/user/userUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void inabilityGetUserByNameOnPageNotExists() throws Exception {
        this.mockMvc.perform(get("/api/user/find")
                .param("name", "t")
                .param("page", "13")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }


    //---------------------------------------------------------------
    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestUserTagReputationOverMonth() throws Exception {

        PageDto<UserDtoList, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(5);
        expected.setItemsOnPage(10);

        List<UserDtoList> expectedItems = new ArrayList<>();
        expectedItems.add(new UserDtoList(1L, "Teat", null, 2, Arrays.asList(new TagDto[]{new TagDto(1L, "java", "Java is a popular high-level programming language."), new TagDto(3L, "html", "HTML (HyperText Markup Language) is the markup language for creating web pages and other information to be displayed in a web browser.")})));
        expectedItems.add(new UserDtoList(2L, "Tot", null, 2, Arrays.asList(new TagDto[]{new TagDto(1L, "java", "Java is a popular high-level programming language."), new TagDto(2L, "javaScript", "For questions regarding programming in ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript)."), new TagDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases.")})));
        expectedItems.add(new UserDtoList(3L, "Tot", null, 2, Arrays.asList(new TagDto[]{new TagDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases.")})));
        expectedItems.add(new UserDtoList(4L, "Tot", null, 2, Arrays.asList(new TagDto[]{})));
        expectedItems.add(new UserDtoList(5L, "Tot", null, 2, Arrays.asList(new TagDto[]{})));
        expected.setItems(expectedItems);
        String resultContext =
                mockMvc.perform(get("/api/user/order/reputation/month")
                        .param("page", "1")
                        .param("size", "10"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                        .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                        .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                        .andExpect(jsonPath("$.items").isNotEmpty())
                        .andExpect(jsonPath("$.itemsOnPage").isNotEmpty())
                        .andReturn().getResponse().getContentAsString();

        PageDto<UserDtoList, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.getClass(), actual.getClass());
        Assertions.assertEquals(expected.getCurrentPageNumber(), actual.getCurrentPageNumber());
        Assertions.assertEquals(expected.getTotalPageCount(), actual.getTotalPageCount());
        Assertions.assertEquals(expected.getTotalResultCount(), actual.getTotalResultCount());
        Assertions.assertEquals(expected.getItemsOnPage(), actual.getItemsOnPage());
        Assertions.assertEquals(expected.getItems().size(), actual.getItems().size());
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestNegativePageUserTagReputationOverMonth() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/month")
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestNegativeSizeUserTagReputationOverMonth() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/month")
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestIncorrectSizeUserTagReputationOverMonth() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/month")
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestPageDontExistsUserTagReputationOverMonth() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/month")
                .param("page", "99")
                .param("size", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    //------------------- UserOrderReputationYear --------------------//
    @Test
    void requestPageUserReputationOverYearWithStatusOk() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/year")
                .param("page", "1")
                .param("size", "100"))
                .andExpect(status().isOk());
    }

    @Test
    void requestNegativePageUserReputationOverYear() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/year")
                .param("page", "-1")
                .param("size", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @Test
    void requestNegativeSizeUserReputationOverYear() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/year")
                .param("page", "1")
                .param("size", "-100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @Test
    void requestIncorrectSizeUserReputationOverYear() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/year")
                .param("page", "1")
                .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100"));
    }

    @Test
    void requestUserReputationOverYear() throws Exception {
        mockMvc.perform(get("/api/user/order/reputation/year")
                .param("page", "1")
                .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isArray());
    }

    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
        // user 4 ?
    void requestUserPasswordResetStatusOk() throws Exception {
        UserResetPasswordDto ps = new UserResetPasswordDto();
        ps.setOldPassword("password0");
        ps.setNewPassword("user");
        String jsonRequest = objectMapper.writeValueAsString(ps);

        this.mockMvc.perform(post("/api/user/password/reset")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Password reset successfully"))
                .andExpect(status().isOk());
    }

    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestUserPasswordResetOldPasswordError() throws Exception {
        UserResetPasswordDto ps = new UserResetPasswordDto();
        ps.setOldPassword("errorPass");
        ps.setNewPassword("user");
        String jsonRequest = objectMapper.writeValueAsString(ps);

        this.mockMvc.perform(post("/api/user/password/reset")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Old password is incorrect"))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestUserPasswordResetOldPasswordNull() throws Exception {
        UserResetPasswordDto ps = new UserResetPasswordDto();
        ps.setOldPassword("");
        ps.setNewPassword("user");
        String jsonRequest = objectMapper.writeValueAsString(ps);

        this.mockMvc.perform(post("/api/user/password/reset")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("resetPassword.userResetPasswordDto.oldPassword: Поле не должно быть пустым"))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestUserPasswordResetNewPasswordNull() throws Exception {
        UserResetPasswordDto ps = new UserResetPasswordDto();
        ps.setOldPassword("password0");
        ps.setNewPassword("");
        String jsonRequest = objectMapper.writeValueAsString(ps);

        this.mockMvc.perform(post("/api/user/password/reset")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("resetPassword.userResetPasswordDto.newPassword: Поле не должно быть пустым"))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = {"dataset/user/userPublicInfoApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void updatesUserPublicInfo() throws Exception {

        UserPublicInfoDto userPublicInfoDto = new UserPublicInfoDto();
        userPublicInfoDto.setNickname("BestJavaProgrammer");
        userPublicInfoDto.setAbout("Best Java Programmer ever");
        userPublicInfoDto.setLinkImage("https://www.google.com/search?q=D0");
        userPublicInfoDto.setLinkSite("https://www.yandex.ru");
        userPublicInfoDto.setLinkVk("https://www.vk.com");
        userPublicInfoDto.setLinkGitHub("https://www.github.com");
        userPublicInfoDto.setFullName("Teat");
        userPublicInfoDto.setCity("Moscow");
        String jsonRequest = objectMapper.writeValueAsString(userPublicInfoDto);

        this.mockMvc.perform(post("/api/user/public/info")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String hql = "FROM User AS u WHERE u.id = 153L";
        User user = (User) entityManager.createQuery(hql).getResultList().get(0);

        assert (userPublicInfoDto.getNickname().equals(user.getNickname()) &&
                userPublicInfoDto.getAbout().equals(user.getAbout()) &&
                userPublicInfoDto.getLinkImage().equals(user.getImageLink()) &&
                userPublicInfoDto.getLinkSite().equals(user.getLinkSite()) &&
                userPublicInfoDto.getLinkVk().equals(user.getLinkVk()) &&
                userPublicInfoDto.getLinkGitHub().equals(user.getLinkGitHub()) &&
                userPublicInfoDto.getFullName().equals(user.getFullName()) &&
                userPublicInfoDto.getCity().equals(user.getCity()));


    }

    @ParameterizedTest
    @CsvSource({
            " , someFullName",
            "'   ', someFullName",
            "someNickname, ",
            "someNickname, '    '",
    })
    void ifRequiredFieldsNullOrBlankThenBadRequest(String nickname, String fullName) throws Exception {
        UserPublicInfoDto userPublicInfoDto = new UserPublicInfoDto();
        userPublicInfoDto.setNickname(nickname);
        userPublicInfoDto.setFullName(fullName);
        userPublicInfoDto.setAbout("Something about");
        String jsonRequest = objectMapper.writeValueAsString(userPublicInfoDto);

        this.mockMvc.perform(post("/api/user/public/info")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = {"dataset/user/userPublicInfoApi.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void ifFrontSendsWrongIdThenCorrectsIdAndUpdatesPrincipal() throws Exception {
        UserPublicInfoDto userPublicInfoDto = new UserPublicInfoDto();
        userPublicInfoDto.setId(42L);
        userPublicInfoDto.setNickname("BestJavaProgrammer");
        userPublicInfoDto.setAbout("Best Java Programmer ever");
        userPublicInfoDto.setLinkImage("https://www.google.com/search?q=D0");
        userPublicInfoDto.setLinkSite("https://www.yandex.ru");
        userPublicInfoDto.setLinkVk("https://www.vk.com");
        userPublicInfoDto.setLinkGitHub("https://www.github.com");
        userPublicInfoDto.setFullName("Teat");
        userPublicInfoDto.setCity("Moscow");
        String jsonRequest = objectMapper.writeValueAsString(userPublicInfoDto);

        this.mockMvc.perform(post("/api/user/public/info")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String hql = "FROM User AS u where u.id = 153L";
        User user = (User) entityManager.createQuery(hql).getResultList().get(0);

        assert (userPublicInfoDto.getNickname().equals(user.getNickname()) &&
                userPublicInfoDto.getAbout().equals(user.getAbout()) &&
                userPublicInfoDto.getLinkImage().equals(user.getImageLink()) &&
                userPublicInfoDto.getLinkSite().equals(user.getLinkSite()) &&
                userPublicInfoDto.getLinkVk().equals(user.getLinkVk()) &&
                userPublicInfoDto.getLinkGitHub().equals(user.getLinkGitHub()) &&
                userPublicInfoDto.getFullName().equals(user.getFullName()) &&
                userPublicInfoDto.getCity().equals(user.getCity()));

        hql = "FROM User AS u WHERE u.id = 42L";
        User wrongUser = (User) entityManager.createQuery(hql).getResultList().get(0);

        assert (wrongUser.getNickname().equals("wrong_user") &&
                wrongUser.getAbout().equals("Something about wrong user") &&
                wrongUser.getImageLink().equals("wrong image") &&
                wrongUser.getLinkSite().equals("wrong site") &&
                wrongUser.getLinkVk().equals("wrong vk") &&
                wrongUser.getLinkGitHub().equals("wrong git") &&
                wrongUser.getFullName().equals("wrong fullname") &&
                wrongUser.getCity().equals("wrong city"));
    }

    @DataSet(value = {"dataset/user/user153.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestUserDelete() throws Exception {
        mockMvc.perform(delete(DELETE))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @DataSet(value = {"dataset/user/userDeleted.yml", "dataset/user/roleUserApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestDeleteAlreadyDeletedUser() throws Exception {
        mockMvc.perform(delete(DELETE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(BAD_REQUEST_MESSAGE_ALREADY_DELETED));
    }

    @Test
    @DataSet(value = "dataset/user/roleUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void userPersistedWithoutConfirmStatusOk() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("some@with.email");
        user.setPassword("100500");
        user.setFullName("Samuel Smith");

        String jsonRequest = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/api/user/persistWithoutConfirm")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(user.email))
                .andExpect(jsonPath("fullName").value(user.fullName))
                .andExpect(jsonPath("linkImage").isEmpty())
                .andExpect(jsonPath("reputation").value(0));

        TypedQuery<User> userQuery = entityManager.createQuery("FROM User WHERE email =: email", User.class)
                .setParameter("email", "some@with.email");

        Optional<User> newUser = SingleResultUtil.getSingleResultOrNull(userQuery);
        Assertions.assertNotNull(newUser);
        TypedQuery<Reputation> reputationQuery = entityManager.createQuery("FROM Reputation WHERE user.id =: userId", Reputation.class)
                .setParameter("userId", newUser.get().getId());
        Assertions.assertNotNull(SingleResultUtil.getSingleResultOrNull(reputationQuery));
        // ожидаем 200 а приходит 401 ответ
    }

    @Test
    @DataSet(value = {"dataset/user/userApi.yml", "dataset/user/roleUserApi.yml"}, disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void userNotPersistedWithoutConfirmCuzAlreadyExist() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("ivanov@mail.ru");
        user.setPassword("1234");
        user.setFullName("Peter Parker");
        String jsonRequest = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/user/persistWithoutConfirm")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(String.format("User with email %s already exist", user.getEmail())));
    }
    @Test
    @DataSet(value = "dataset/user/roleUserApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void isIncludedInGroupChat() throws Exception {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setEmail("11@22.ru");
        user.setPassword("100");
        user.setFullName("Ivan Ivanich");

        Long chatId = 1l;

        String jsonRequest = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/api/auth/reg/confirm")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(user.email))
                .andExpect(jsonPath("fullName").value(user.fullName))
                .andExpect(jsonPath("linkImage").isEmpty())
                .andExpect(status().isOk());

        TypedQuery<User> createdUser = entityManager.createQuery("FROM User WHERE email =: email", User.class)
                .setParameter("email", "11@22.ru");

        User newUser = SingleResultUtil.getSingleResultOrNull(createdUser).get();
        Assertions.assertNotNull(newUser);
        TypedQuery<User> addedToGroupChat= entityManager.createQuery("FROM GroupChat WHERE chat.id =: chatId and user.id =: userId", User.class)
                .setParameter("userId", newUser.getId())
                .setParameter("chatId", chatId);
        Assertions.assertEquals(createdUser,addedToGroupChat);
    }

    @DataSet(value = {"dataset/question/roleQuestionApi.yml",
            "dataset/user/usersQuestionApi.yml",
            "dataset/question/questionQuestionApi.yml",
            "dataset/question/tagQuestionApi.yml",
            "dataset/question/question_has_tagQuestionApi.yml"}, cleanBefore = true, cleanAfter = true)
    @Test
    void requestReputationHistory() throws Exception {

        int id = 1;

        PageDto<ReputationHistoryDtoList, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(5);
        expected.setTotalResultCount(5);
        expected.setItemsOnPage(1);

        List<ReputationHistoryDtoList> expectedItems = new ArrayList<>();
        expectedItems.add(new ReputationHistoryDtoList(1L, 2, ReputationType.Question, LocalDateTime.of(2021, Month.MARCH, 20, 21, 22, 13)));
        expected.setItems(expectedItems);
        String resultContext =
                mockMvc.perform(get("/api/user/reputation/history/" + id)
                        .param("page", "1")
                        .param("size", "1"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                        .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                        .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                        .andExpect(jsonPath("$.items").isNotEmpty())
                        .andExpect(jsonPath("$.itemsOnPage").isNotEmpty())
                        .andReturn().getResponse().getContentAsString();

        PageDto<ReputationHistoryDtoList, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.getClass(), actual.getClass());
        Assertions.assertEquals(expected.getCurrentPageNumber(), actual.getCurrentPageNumber());
        Assertions.assertEquals(expected.getTotalPageCount(), actual.getTotalPageCount());
        Assertions.assertEquals(expected.getTotalResultCount(), actual.getTotalResultCount());
        Assertions.assertEquals(expected.getItemsOnPage(), actual.getItemsOnPage());
        Assertions.assertEquals(expected.getItems().size(), actual.getItems().size());

    }

    @Test
    @DataSet(value = "dataset/user/userAnswerApi.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void requestGetAnswerListPrincipalUser() throws Exception {
        PageDto<AnswerDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(3);
        expected.setItemsOnPage(10);

        List<AnswerDto> expectedItems = new ArrayList<>();
        expectedItems.add(new com.javamentor.qa.platform.models.dto.AnswerDto(14L, 153L, 1L, "This is answer for question number one",  LocalDateTime.of(2020,1,1,13,58,56),
                false, LocalDateTime.of(2020,1,1,13,58,56) , 0L, "https://www.google.com/search?q=D0", "Teat" ));
        expectedItems.add(new AnswerDto(20L, 153L, 2L, "This is answer for question number two",  LocalDateTime.of(2020,1,1,13,58,56),
                false, LocalDateTime.of(2020,1,1,13,58,56), 0L, "https://www.google.com/search?q=D0", "Teat" ));
        expectedItems.add(new AnswerDto(30L, 153L, 3L, "This is answer for question number three",  LocalDateTime.of(2020,1,1,13,58,56),
                false, LocalDateTime.of(2020,1,1,13,58,56), 0L, "https://www.google.com/search?q=D0", "Teat" ));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get("/api/user/currentUser/answers")
                .param("page", "1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.itemsOnPage").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        PageDto<AnswerDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testUserQuestionsSortedByVotes() throws Exception {

        this.mockMvc.perform(get("/api/user/order/questions/votes/1")
                .param("page", "1")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPageNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPageCount").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalResultCount").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andDo(print())
                .andExpect(status().isOk());
    }
}
