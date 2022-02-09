package com.javamentor.qa.platform.webapp.converters;

import com.javamentor.qa.platform.models.dto.GroupChatDto;
import com.javamentor.qa.platform.models.dto.UserDto;
import com.javamentor.qa.platform.models.entity.chat.GroupChat;
import com.javamentor.qa.platform.models.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class  GroupChatConverter {

    @Autowired
    private UserConverter userConverter;

    @Mapping(source = "groupChat.id", target = "id")
    @Mapping(source = "groupChat.chat.id", target = "chatId")
    @Mapping(source = "groupChat.chat.title", target = "title")
    @Mapping(source = "groupChat.users", target = "users", qualifiedByName = "groupChatSetToGroupChatDtoSet")
    public abstract GroupChatDto groupChatToGroupChatDto(GroupChat groupChat);

    @Named("groupChatSetToGroupChatDtoSet")
    public Set<UserDto> groupChatSetToGroupChatDtoSet(Set<User> userSet){
        return userSet.stream().map(e -> userConverter.userToUserDto(e)).collect(Collectors.toSet());
    }
}
