package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.TopUsersByTagDtoDao;
import com.javamentor.qa.platform.models.dto.UserDto;
import com.javamentor.qa.platform.service.abstracts.dto.TopUsersByTagDtoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TopUsersByTagDtoServiceImpl implements TopUsersByTagDtoService {

    private final TopUsersByTagDtoDao topUsersByTagDtoDao;

    public TopUsersByTagDtoServiceImpl(TopUsersByTagDtoDao topUsersByTagDtoDao) {
        this.topUsersByTagDtoDao = topUsersByTagDtoDao;
    }

    @Override
    public Optional<UserDto> getTopUsersDtoByTagId(Long tagId) {
        return topUsersByTagDtoDao.getTopUserByTagIdDto(tagId);
    }
}
