package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.GroupChatDao;
import com.javamentor.qa.platform.models.entity.chat.GroupChat;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class GroupChatDaoImpl extends ReadWriteDaoImpl<GroupChat, Long> implements GroupChatDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsGroupChatByTitleAndId(String title, Long userId) {
        return (entityManager.unwrap(Session.class)
                .createNativeQuery("select ch.title, gu.user_id " +
                        "from Chat ch " +
                        "join groupchat_has_users gu on gu.chat_id = ch.id and gu.user_id = :userId " +
                        "join group_chat gc on ch.id = gc.chat_id " +
                        "where ch.title = :title and gu.user_id = :userId")
                .setParameter("title", title)
                .setParameter("userId", userId)
                .unwrap(Query.class)
                .getResultList()).isEmpty();
    }
}
