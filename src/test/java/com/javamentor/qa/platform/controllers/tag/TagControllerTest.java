package com.javamentor.qa.platform.controllers.tag;

import com.github.database.rider.core.api.dataset.DataSet;
import com.javamentor.qa.platform.AbstractIntegrationTest;
import com.javamentor.qa.platform.models.dto.*;
import com.javamentor.qa.platform.models.entity.question.Tag;
import com.javamentor.qa.platform.models.entity.question.TrackedTag;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.webapp.converters.TagTrackedConverter;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DataSet(value = {"dataset/question/roleQuestionApi.yml",
        "dataset/question/usersQuestionApi.yml",
        "dataset/question/questionQuestionApi.yml",
        "dataset/question/tagQuestionApi.yml",
        "dataset/question/question_has_tagQuestionApi.yml",
        "dataset/tag/tracked_tag.yml",
        "dataset/tag/ignored_tag.yml"}
        , cleanBefore = true, cleanAfter = true)
@WithMockUser(username = "principal@mail.ru", roles = {"ADMIN", "USER"})
@ActiveProfiles("local")
class TagControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String POPULAR = "/api/tag/popular";
    private static final String RECENT = "/api/tag/recent";
    private static final String ALPHABET = "/api/tag/alphabet/order";
    private static final String ORDER_POPULAR = "/api/tag/order/popular";
    private static final String NAME = "/api/tag/name";
    private static final String NEW_TAG = "/api/tag/new/order";
    private static final String BAD_REQUEST_MESSAGE = "Номер страницы и размер должны быть положительными. Максимальное количество записей на странице 100";
    private static final String REQUEST_PARAM_FOR_ADD_TAGS = "name";

    // Тесты запросов популярных тэгов
    @Test
    void requestGetTagDtoPaginationByPopular() throws Exception {
        PageDto<TagDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(6);
        expected.setItemsOnPage(10);

        List<TagDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagDto(1L, "java", "Java is a popular high-level programming language."));
        expectedItems.add(new TagDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases."));
        expectedItems.add(new TagDto(2L, "javaScript", "For questions regarding programming in " +
                "ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript)."));
        expectedItems.add(new TagDto(3L, "html", "HTML (HyperText Markup Language) is the markup language for " +
                "creating web pages and other information to be displayed in a web browser."));
        expectedItems.add(new TagDto(4L, "bootstrap-4", "Bootstrap 4 is the fourth major version of " +
                "the popular front-end component library. The Bootstrap framework aids in the creation of responsive, mobile-first websites and web apps."));
        expectedItems.add(new TagDto(6L, "sql22", "aaStructured Query Language (SQL) is a language for querying databases."));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(POPULAR)
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

        PageDto<TagDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagDtoPaginationByPopular() throws Exception {
        mockMvc.perform(get(POPULAR)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestNegativeSizeGetTagDtoPaginationByPopular() throws Exception {
        mockMvc.perform(get(POPULAR)
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestIncorrectSizeGetTagDtoPaginationByPopular() throws Exception {
        mockMvc.perform(get(POPULAR)
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetTagDtoPaginationByPopular() throws Exception {
        mockMvc.perform(get(POPULAR)
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


    // Тесты запросов недавних тэгов
    @Test
    void requestGetTagRecentDtoPagination() throws Exception {
        PageDto<TagRecentDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(6);
        expected.setItemsOnPage(10);

        List<TagRecentDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagRecentDto(1L, "java", 0));
        expectedItems.add(new TagRecentDto(5L, "sql", 0));
        expectedItems.add(new TagRecentDto(2L, "javaScript", 0));
        expectedItems.add(new TagRecentDto(3L, "html", 0));
        expectedItems.add(new TagRecentDto(4L, "bootstrap-4", 0));
        expectedItems.add(new TagRecentDto(6L, "sql22", 0));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(RECENT)
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

        PageDto<TagRecentDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagRecentDtoPagination() throws Exception {
        mockMvc.perform(get(RECENT)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestNegativeSizeGetTagRecentDtoPagination() throws Exception {
        mockMvc.perform(get(RECENT)
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestIncorrectSizeGetTagRecentDtoPagination() throws Exception {
        mockMvc.perform(get(RECENT)
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetTagRecentDtoPagination() throws Exception {
        mockMvc.perform(get(RECENT)
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


    // Тесты запросов тэгов по алфавиту
    @Test
    void requestGetTagDtoPaginationOrderByAlphabet() throws Exception {
        PageDto<TagListDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(6);
        expected.setItemsOnPage(10);

        List<TagListDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagListDto(4L, "bootstrap-4", "Bootstrap 4 is the fourth major version of the popular front-end component library. The Bootstrap framework aids in the creation of responsive, mobile-first websites and web apps.", 0, 0, 0));
        expectedItems.add(new TagListDto(3L, "html", "HTML (HyperText Markup Language) is the markup language for creating web pages and other information to be displayed in a web browser.", 1, 0, 0));
        expectedItems.add(new TagListDto(1L, "java", "Java is a popular high-level programming language.", 3, 0, 0));
        expectedItems.add(new TagListDto(2L, "javaScript", "For questions regarding programming in ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript).", 2, 0, 0));
        expectedItems.add(new TagListDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases.", 3, 0, 0));
        expectedItems.add(new TagListDto(6L, "sql22", "aaStructured Query Language (SQL) is a language for querying databases.", 0, 0, 0));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(ALPHABET)
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

        PageDto<TagListDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagDtoPaginationOrderByAlphabet() throws Exception {
        mockMvc.perform(get(ALPHABET)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestNegativeSizeGetTagDtoPaginationOrderByAlphabet() throws Exception {
        mockMvc.perform(get(ALPHABET)
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestIncorrectSizeGetTagDtoPaginationOrderByAlphabet() throws Exception {
        mockMvc.perform(get(ALPHABET)
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetTagDtoPaginationOrderByAlphabet() throws Exception {
        mockMvc.perform(get(ALPHABET)
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


    // Тест order popular
    @Test
    void requestGetTagListDtoByPopularPagination() throws Exception {
        PageDto<TagListDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(6);
        expected.setItemsOnPage(10);

        List<TagListDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagListDto(1L, "java", "Java is a popular high-level programming language.", 3, 0, 0));
        expectedItems.add(new TagListDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases.", 3, 0, 0));
        expectedItems.add(new TagListDto(2L, "javaScript", "For questions regarding programming in ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript).", 2, 0, 0));
        expectedItems.add(new TagListDto(3L, "html", "HTML (HyperText Markup Language) is the markup language for creating web pages and other information to be displayed in a web browser.", 1, 0, 0));
        expectedItems.add(new TagListDto(4L, "bootstrap-4", "Bootstrap 4 is the fourth major version of the popular front-end component library. The Bootstrap framework aids in the creation of responsive, mobile-first websites and web apps.", 0, 0, 0));
        expectedItems.add(new TagListDto(6L, "sql22", "aaStructured Query Language (SQL) is a language for querying databases.", 0, 0, 0));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(ORDER_POPULAR)
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

        PageDto<TagListDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagListDtoByPopularPagination() throws Exception {
        mockMvc.perform(get(ORDER_POPULAR)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestNegativeSizeGetTagListDtoByPopularPagination() throws Exception {
        mockMvc.perform(get(ORDER_POPULAR)
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestIncorrectSizeGetTagListDtoByPopularPagination() throws Exception {
        mockMvc.perform(get(ORDER_POPULAR)
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetTagListDtoByPopularPagination() throws Exception {
        mockMvc.perform(get(ORDER_POPULAR)
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


    @Test
    void requestGetTagName() throws Exception {
        PageDto<TagListDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(2);
        expected.setItemsOnPage(10);

        List<TagListDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagListDto(
                1L,
                "java",
                "Java is a popular high-level programming language.",
                3L, 0, 0));
        expectedItems.add(new TagListDto(
                2L,
                "javaScript",
                "For questions regarding programming in ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript).",
                2L, 0, 0));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(NAME)
                .param("name", "java")
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

        PageDto<TagListDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagName() throws Exception {
        mockMvc.perform(get(NAME)
                .param("name", "java")
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestNegativeSizeGetTagName() throws Exception {
        mockMvc.perform(get(NAME)
                .param("name", "java")
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestIncorrectSizeGetTagName() throws Exception {
        mockMvc.perform(get(NAME)
                .param("name", "java")
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetTagName() throws Exception {
        mockMvc.perform(get(NAME)
                .param("name", "java")
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

    // Тесты запроса дочерних тегов по id
    @Test
    @DataSet(value = {"dataset/tag/related_tag.yml"}
            , cleanBefore = true, cleanAfter = true)
    void requestChildTagByMainTagId() throws Exception {
        PageDto<TagRecentDto, Object> expected = new PageDto<>();
        List<TagRecentDto> expectedChild = new ArrayList<>();
        expectedChild.add(new TagRecentDto(5L, "Child", 1L));
        expected.setItems(expectedChild);
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(1);
        expected.setItemsOnPage(10);

        long id = 4L;

        String resultContext = mockMvc.perform(get("/api/tag/{id}/child", id)
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

        PageDto<TagRecentDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagRecentDtoChildTagById() throws Exception {
        long id = 4L;
        mockMvc.perform(get("/api/tag/{id}/child", id)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestChildTagsWithWrongTagId() throws Exception {
        long id = 500L;
        mockMvc.perform(get("/api/tag/{id}/child", id)
                .param("page", "1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.itemsOnPage").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void requestTooBigSizeToChildEndpoint() throws Exception {
        long id = 4L;
        mockMvc.perform(get("/api/tag/{id}/child", id)
                .param("page", "1")
                .param("size", "105"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    @Test
    void requestPageDontExistsGetPageWithEmptyTagRecentDto() throws Exception {
        long id = 4L;
        mockMvc.perform(get("/api/tag/{id}/child", id)
                .param("page", "15")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPageNumber").isNotEmpty())
                .andExpect(jsonPath("$.totalPageCount").isNotEmpty())
                .andExpect(jsonPath("$.totalResultCount").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    // Test order by new Tag
    @Test
    void requestGetTagDtoPaginationByNewTag() throws Exception {
        PageDto<TagListDto, Object> expected = new PageDto<>();
        expected.setCurrentPageNumber(1);
        expected.setTotalPageCount(1);
        expected.setTotalResultCount(6);
        expected.setItemsOnPage(10);

        List<TagListDto> expectedItems = new ArrayList<>();
        expectedItems.add(new TagListDto(1L, "java", "Java is a popular high-level programming language.", 3, 0, 0));
        expectedItems.add(new TagListDto(5L, "sql", "Structured Query Language (SQL) is a language for querying databases.", 3, 0, 0));
        expectedItems.add(new TagListDto(2L, "javaScript", "For questions regarding programming in ECMAScript (JavaScript/JS) and its various dialects/implementations (excluding ActionScript).", 2, 0, 0));
        expectedItems.add(new TagListDto(3L, "html", "HTML (HyperText Markup Language) is the markup language for creating web pages and other information to be displayed in a web browser.", 1, 0, 0));
        expectedItems.add(new TagListDto(4L, "bootstrap-4", "Bootstrap 4 is the fourth major version of the popular front-end component library. The Bootstrap framework aids in the creation of responsive, mobile-first websites and web apps.", 0, 0, 0));
        expectedItems.add(new TagListDto(6L, "sql22", "aaStructured Query Language (SQL) is a language for querying databases.", 0, 0, 0));
        expected.setItems(expectedItems);

        String resultContext = mockMvc.perform(get(NEW_TAG)
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

        PageDto<TagListDto, Object> actual = objectMapper.readValue(resultContext, PageDto.class);
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void requestNegativePageGetTagDtoPaginationByNewTag() throws Exception {
        mockMvc.perform(get(NEW_TAG)
                .param("page", "-1")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Page and Size have to be positive. Max number of items per page 100"));
    }

    @Test
    void requestNegativeSizeGetTagDtoPaginationByNewTag() throws Exception {
        mockMvc.perform(get(NEW_TAG)
                .param("page", "1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Page and Size have to be positive. Max number of items per page 100"));
    }

    @Test
    void requestIncorrectSizeGetTagDtoPaginationByNewTag() throws Exception {
        mockMvc.perform(get(NEW_TAG)
                .param("page", "1")
                .param("size", "101"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("text/plain;charset=UTF-8"))
                .andExpect(content().string("Page and Size have to be positive. Max number of items per page 100"));
    }

    @Test
    void requestPageDontExistsGetTagDtoPaginationByNewTag() throws Exception {
        mockMvc.perform(get(NEW_TAG)
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

    @Test
    void addTagTrackedStatusOk() throws Exception {

        final String TAG_NAME = "java";

        //достаем тэг из бд
        Tag tag = entityManager
                .createQuery("from Tag where name = :name", Tag.class)
                .setParameter("name", TAG_NAME)
                .getSingleResult();

        this.mockMvc.perform(post("/api/tag/tracked/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                .andExpect(status().isOk());

        //проверяем что этот тэг добавлен в отслеживаемые
        BigInteger trackedTagId = null;
        try {
            Query query = entityManager.createNativeQuery("select id from tag_tracked where tracked_tag_id = ?");
            query.setParameter(1, tag.getId());
            trackedTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            throw nre;
        }
    }

    @Test
    void addTagTrackedStatusNotExistOnThisSite() throws Exception {

        final String TAG_NAME = "Tag Name11";

        Tag tag = null;
        try {
            //достаем тэг из бд
            tag = entityManager
                    .createQuery("from Tag where name = :name", Tag.class)
                    .setParameter("name", TAG_NAME)
                    .getSingleResult();
        } catch (NoResultException nre) {
            //ignore
        }

        if (tag == null) {
            this.mockMvc.perform(post("/api/tag/tracked/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The Tag Name11 does not exist on this site"));
        }

    }

    @Test
    void addTagTrackedStatusHasAlreadyBeenAdded() throws Exception {

        final String TAG_NAME = "java";

        //достаем тэг из бд
        Tag tag = entityManager
                .createQuery("from Tag where name = :name", Tag.class)
                .setParameter("name", TAG_NAME)
                .getSingleResult();

        //проверяем что этот тэг не добавлен в отслеживаемые
        BigInteger trackedTagId = null;
        try {
            Query query = entityManager.createNativeQuery("select id from tag_tracked where tracked_tag_id = ?");
            query.setParameter(1, tag.getId());
            trackedTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            //ignore
        }

        //добавляем тэг в отслеживаемые
        if (trackedTagId == null) {
            this.mockMvc.perform(post("/api/tag/tracked/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isOk());
        } else {
            this.mockMvc.perform(post("/api/tag/tracked/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The tracked tag has already been added"));
        }

        //проверяем что этот тэг добавлен в отслеживаемые
        try {
            Query query = entityManager.createNativeQuery("select id from tag_tracked where tracked_tag_id = ?");
            query.setParameter(1, tag.getId());
            trackedTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            throw nre;
        }

        //тестируем ответ "The tracked tag has already been added"
        this.mockMvc.perform(post("/api/tag/tracked/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The tracked tag has already been added"));

    }

    @Test
    void addTagIgnoredStatusOk() throws Exception {

        final String TAG_NAME = "java";

        //достаем тэг из бд
        Tag tag = entityManager
                .createQuery("from Tag where name = :name", Tag.class)
                .setParameter("name", TAG_NAME)
                .getSingleResult();

        this.mockMvc.perform(post("/api/tag/ignored/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                .andExpect(status().isOk());

        //проверяем что этот тэг добавлен в отслеживаемые
        BigInteger ignoredTagId = null;
        try {
            Query query = entityManager.createNativeQuery("select id from tag_ignore where ignored_tag_id = ?");
            query.setParameter(1, tag.getId());
            ignoredTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            throw nre;
        }
    }

    @Test
    void addTagIgnoredStatusNotExistOnThisSite() throws Exception {

        final String TAG_NAME = "Tag Name11";

        Tag tag = null;
        try {
            //достаем тэг из бд
            tag = entityManager
                    .createQuery("from Tag where name = :name", Tag.class)
                    .setParameter("name", TAG_NAME)
                    .getSingleResult();
        } catch (NoResultException nre) {
            //ignore
        }

        if (tag == null) {
            this.mockMvc.perform(post("/api/tag/ignored/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The Tag Name11 does not exist on this site"));
        }
    }

    @Test
    void addTagIgnoredStatusHasAlreadyBeenAdded() throws Exception {

        final String TAG_NAME = "java";

        //достаем тэг из бд
        Tag tag = entityManager
                .createQuery("from Tag where name = :name", Tag.class)
                .setParameter("name", TAG_NAME)
                .getSingleResult();

        //проверяем что этот тэг не добавлен в отслеживаемые
        BigInteger ignoredTagId = null;
        try {
            Query query = entityManager.createNativeQuery("select id from tag_ignore where ignored_tag_id = ?");
            query.setParameter(1, tag.getId());
            ignoredTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            //ignore
        }

        //добавляем тэг в отслеживаемые
        if (ignoredTagId == null) {
            this.mockMvc.perform(post("/api/tag/ignored/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isOk());
        } else {
            this.mockMvc.perform(post("/api/tag/ignored/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The ignored tag has already been added"));
        }

        //проверяем что этот тэг добавлен в отслеживаемые
        try {
            Query query = entityManager.createNativeQuery("select id from tag_ignore where ignored_tag_id = ?");
            query.setParameter(1, tag.getId());
            ignoredTagId = (BigInteger) query.getSingleResult();
        } catch (NoResultException nre) {
            throw nre;
        }

        //тестируем ответ "The tracked tag has already been added"
        this.mockMvc.perform(post("/api/tag/ignored/add").param(REQUEST_PARAM_FOR_ADD_TAGS, TAG_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The ignored tag has already been added"));
    }
}
