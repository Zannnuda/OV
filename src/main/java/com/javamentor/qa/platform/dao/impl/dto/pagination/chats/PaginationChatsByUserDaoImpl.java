package com.javamentor.qa.platform.dao.impl.dto.pagination.chats;

import com.javamentor.qa.platform.dao.abstracts.dto.pagination.PaginationDao;
import com.javamentor.qa.platform.models.dto.ChatDto;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository(value = "paginationChatByUser")
public class PaginationChatsByUserDaoImpl implements PaginationDao<ChatDto> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ChatDto> getItems(Map<String, Object> parameters) {
        Long userId = (Long)parameters.get("userId");
        int page = (int)parameters.get("page");
        int size = (int)parameters.get("size");

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
                .setParameter("id", userId)
                .unwrap(Query.class)
                .setFirstResult(page * size - size)
                .setMaxResults(size)
                .getResultList();

        return chats;
    }

    @Override
    public int getCount(Map<String, Object> parameters) {
        Long userId = (Long)parameters.get("userId");
        BigInteger gcs = (BigInteger) entityManager.createNativeQuery("SELECT count(*) \n" +
                "FROM groupchat_has_users u\n" +
                "WHERE u.user_id = :id")
                .setParameter("id", userId)
                .getSingleResult();
        BigInteger scs = (BigInteger) entityManager.createNativeQuery("select count(*)\n" +
                "FROM singel_chat as sc\n" +
                "JOIN chat c on c.id = sc.chat_id\n" +
                "WHERE sc.user_one_id = :id OR sc.use_two_id = :id")
                .setParameter("id", userId)
                .getSingleResult();
        return gcs.intValue() + scs.intValue();
    }
}
