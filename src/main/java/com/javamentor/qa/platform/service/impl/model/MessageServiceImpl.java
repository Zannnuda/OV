package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReadWriteDao;
import com.javamentor.qa.platform.models.entity.chat.Message;
import com.javamentor.qa.platform.service.abstracts.model.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ReadWriteServiceImpl<Message, Long> implements MessageService {
    public MessageServiceImpl(ReadWriteDao<Message, Long> readWriteDao) {
        super(readWriteDao);
    }
}
