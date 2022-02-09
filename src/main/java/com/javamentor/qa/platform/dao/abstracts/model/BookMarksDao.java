package com.javamentor.qa.platform.dao.abstracts.model;

import com.javamentor.qa.platform.models.entity.BookMarks;

import java.util.Optional;

public interface BookMarksDao extends ReadWriteDao<BookMarks, Long>{
    Optional<BookMarks> getBookmarkByUserId(Long userId);
    Optional<BookMarks> getBookmarkByQuestionId(Long questionId, Long userId);
}
