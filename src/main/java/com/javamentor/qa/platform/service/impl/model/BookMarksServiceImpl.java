package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.BookMarksDao;
import com.javamentor.qa.platform.models.entity.BookMarks;
import com.javamentor.qa.platform.service.abstracts.model.BookMarksService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookMarksServiceImpl extends ReadWriteServiceImpl<BookMarks, Long> implements BookMarksService {

    private final BookMarksDao bookMarksDao;

    public BookMarksServiceImpl(BookMarksDao bookMarksDao) {
        super(bookMarksDao);
        this.bookMarksDao = bookMarksDao;
    }

    @Override
    public Optional<BookMarks> getBookmarkByUserId(Long userId) {
        return bookMarksDao.getBookmarkByUserId(userId);
    }

    @Override
    public Optional<BookMarks> getBookmarkByQuestionId(Long questionId, Long userId) {
        return bookMarksDao.getBookmarkByQuestionId(questionId, userId);
    }

}
