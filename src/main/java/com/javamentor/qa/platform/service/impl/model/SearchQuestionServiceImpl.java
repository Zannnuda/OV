package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.SearchQuestionDao;
import com.javamentor.qa.platform.service.abstracts.model.SearchQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchQuestionServiceImpl implements SearchQuestionService {

    private SearchQuestionDao searchQuestionDao;

    @Autowired
    public void setSearchQuestionDao(SearchQuestionDao searchQuestionDao) {
        this.searchQuestionDao = searchQuestionDao;
    }

    @Override
    @Transactional
    public void index() {
        searchQuestionDao.index();
    }

}
