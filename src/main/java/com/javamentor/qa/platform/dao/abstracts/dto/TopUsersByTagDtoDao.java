package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.UserDto;

import java.util.Optional;

public interface TopUsersByTagDtoDao {

    Optional<UserDto> getTopUserByTagIdDto(Long id);

}
