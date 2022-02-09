package com.javamentor.qa.platform.service.abstracts.model;

import com.javamentor.qa.platform.models.entity.chat.GroupChat;

public interface GroupChatService extends ReadWriteService<GroupChat, Long>{
    boolean existsGroupChatByTitleAndId(String title, Long userId);
}
