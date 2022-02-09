package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.SearchQuestionDao;
import com.javamentor.qa.platform.models.dto.FoundEntryDto;
import com.javamentor.qa.platform.models.dto.TagDto;
import com.javamentor.qa.platform.models.entity.question.Question;
import com.javamentor.qa.platform.models.util.FoundEntryType;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchQuestionDaoImpl implements SearchQuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void index() {
        SearchSession searchSession = Search.session(entityManager);

        MassIndexer indexer = searchSession.massIndexer().threadsToLoadObjects(4);

        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
