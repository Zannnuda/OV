package com.javamentor.qa.platform.service.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.BookmarkDtoDao;
import com.javamentor.qa.platform.models.dto.BookmarkDto;
import com.javamentor.qa.platform.service.abstracts.dto.BookmarkDtoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkDtoServiceImpl implements BookmarkDtoService {

    private final BookmarkDtoDao bookmarkDtoDao;

    public BookmarkDtoServiceImpl(BookmarkDtoDao bookmarkDtoDao) {
        this.bookmarkDtoDao = bookmarkDtoDao;

    }

    @Override
    public List<BookmarkDto> getBookmarkDtoByUserId(Long id) {
        return bookmarkDtoDao.getBookmarkByUserId(id);
    }
}
