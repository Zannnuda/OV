package com.javamentor.qa.platform.service.abstracts.model;

import com.javamentor.qa.platform.models.entity.BookMarks;
import com.javamentor.qa.platform.models.entity.user.User;

import java.util.Optional;

public interface BookMarksService extends ReadWriteService<BookMarks, Long>{
    Optional<BookMarks> getBookmarkByUserId(Long userId);
    Optional<BookMarks> getBookmarkByQuestionId(Long questionId, Long userId);
}
