package com.javamentor.qa.platform.webapp.converters;


import com.javamentor.qa.platform.models.dto.TagDto;
import com.javamentor.qa.platform.models.entity.question.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TagConverter {
    @Mapping(source = "tag.id", target = "id")
    @Mapping(source = "tag.name", target = "name")
    @Mapping(source = "tag.description", target = "description")
    public abstract TagDto tagToTagDto(Tag tag);

    @Mapping(source = "tagDto.id", target = "id")
    @Mapping(source = "tagDto.name", target = "name")
    @Mapping(source = "tagDto.description", target = "description")
    public abstract Tag tagDtoToTag(TagDto tagDto);
}
