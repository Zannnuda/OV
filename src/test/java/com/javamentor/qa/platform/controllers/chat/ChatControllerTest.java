package com.javamentor.qa.platform.controllers.chat;

import com.github.database.rider.core.api.dataset.DataSet;
import com.javamentor.qa.platform.AbstractIntegrationTest;
import com.javamentor.qa.platform.models.dto.MessageDto;
import com.javamentor.qa.platform.models.dto.PageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser(username = "principal@mail.ru", roles = {"ADMIN", "USER"})
@ActiveProfiles("local")
class ChatControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DataSet(value = "dataset/message/message.yml", disableConstraints = true, cleanBefore = true, cleanAfter = true)
    void getAllMessageByChatIdPagination() throws Exception {

        PageDto<MessageDto, Object> expect = new PageDto<>();
        expect.setTotalResultCount(7);
        expect.setItemsOnPage(5);
        expect.setCurrentPageNumber(1);
        expect.setTotalPageCount(2);
        expect.setMeta(null);

        List<MessageDto> items = new ArrayList<>();

        items.add(new MessageDto(4L, "Text group message 4",
                LocalDateTime.of(2020, 10, 25, 13, 58, 59),
                LocalDateTime.of(2020, 01, 02, 13, 58, 59), 4L, 4L, "https://pbs.twimg.com/profile_images/1182694005408186375/i5xT6juJ_400x400.jpg"));
        items.add(new MessageDto(3L, "Text group message 3",
                LocalDateTime.of(2020, 10, 25, 13, 58, 57),
                LocalDateTime.of(2020, 01, 02, 13, 58, 57), 2L, 4L, "https://www.java-mentor.com/images/jm-logo-sq.png"));

        expect.setItems(items);

        String jsonResult = objectMapper.writeValueAsString(expect);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/chat/4/message")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(jsonResult));
    }
}