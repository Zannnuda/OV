package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.UserDto;

import java.util.Optional;

public interface TopUsersByTagDtoService {
    Optional<UserDto> getTopUsersDtoByTagId(Long id);
}
