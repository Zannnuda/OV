package com.javamentor.qa.platform.dao.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.ChatDtoDao;
import com.javamentor.qa.platform.models.dto.ChatDto;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ChatDtoDaoImpl implements ChatDtoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ChatDto> getAllChatsByUser(Long userID) {
        List<ChatDto> chats = (List<ChatDto>) entityManager.unwrap(Session.class)
                .createNativeQuery("SELECT c.id as id, " +
                        "c.title as title, " +
                        "c.persist_date as persistDate, " +
                        "c.chat_type as chatType " +
                        "FROM group_chat as gc " +
                        "JOIN chat c on c.id = gc.chat_id " +
                        "JOIN groupchat_has_users u ON u.chat_id = gc.chat_id " +
                        "WHERE u.user_id = :id " +
                        "UNION ALL " +
                        "SELECT c.id as id, " +
                        "c.title as title, " +
                        "c.persist_date as persistDate, " +
                        "c.chat_type as chatType " +
                        "FROM singel_chat as sc " +
                        "JOIN chat c on c.id = sc.chat_id " +
                        "WHERE sc.user_one_id = :id OR sc.use_two_id = :id")
                .setParameter("id", userID)
                .unwrap(Query.class)
                .getResultList();
        return chats;
    }
}
