package com.javamentor.qa.platform.webapp.converters;

import com.javamentor.qa.platform.models.dto.CreateSingleChatDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;
import com.javamentor.qa.platform.models.entity.chat.SingleChat;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SingleChatConverter {

    @Autowired
    UserService userService;

    @Mapping(source = "singleChat.id", target = "id")
    @Mapping(source = "singleChat.chat.title", target = "title")
    @Mapping(source = "singleChat.userOne.id", target = "userOneId")
    @Mapping(source = "singleChat.useTwo.id", target = "userTwoId")
    @Mapping(source = "singleChat.userOne.id", target = "userSenderId")
    @Mapping(target = "imageLink", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "lastRedactionDate", ignore = true)
    public abstract SingleChatDto singleChatToSingleChatDto(SingleChat singleChat);

    @Mapping(target = "userOne", source = "userOneId", qualifiedByName = "mapUser")
    @Mapping(target = "useTwo", source = "userTwoId", qualifiedByName = "mapUser")
    @Mapping(target = "chat.chatType", source = "chatType")
    @Mapping(source = "title", target = "chat.title")
    public abstract SingleChat createSingleChatDtoToSingleChat(CreateSingleChatDto createSingleChatDto);

    @Named("mapUser")
    public User mapUser(Long id){
        return userService.getById(id).get();
    }
}
