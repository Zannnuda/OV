package com.javamentor.qa.platform.webapp.converters;


import com.javamentor.qa.platform.models.dto.MessageDto;
import com.javamentor.qa.platform.models.entity.chat.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MessageConverter {

    @Mapping(source = "message.id", target = "id")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "message.persistDate", target = "persistDate")
    @Mapping(source = "message.lastRedactionDate", target = "lastRedactionDate")
    @Mapping(source = "message.userSender.id", target = "userSenderId")
    @Mapping(source = "message.chat.id", target = "chatId")
    public abstract MessageDto messageToMessageDTO(Message message);
}
