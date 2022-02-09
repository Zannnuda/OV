package com.javamentor.qa.platform.models.dto;

import com.javamentor.qa.platform.models.util.OnUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateDto {

    @NotNull(groups = OnUpdate.class, message = "Значение title отсутствует")
    @NotBlank(groups = OnUpdate.class, message = "Значение title не должно быть пустым")
    @NotNull
    private String title;

    @NotNull(groups = OnUpdate.class, message = "Значение description отсутствует")
    @NotBlank(groups = OnUpdate.class, message = "Значение description не должно быть пустым")
    @NotNull
    private String description;
}