package com.javamentor.qa.platform.dao.abstracts.model;

import com.javamentor.qa.platform.models.entity.chat.GroupChat;

public interface GroupChatDao extends ReadWriteDao<GroupChat, Long>{
    boolean existsGroupChatByTitleAndId(String title, Long userId);
}
