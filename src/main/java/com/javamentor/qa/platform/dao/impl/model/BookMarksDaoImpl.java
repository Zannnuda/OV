package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.BookMarksDao;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.entity.BookMarks;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class BookMarksDaoImpl extends ReadWriteDaoImpl<BookMarks, Long> implements BookMarksDao {

    private final EntityManager entityManager;

    public BookMarksDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<BookMarks> getBookmarkByUserId(Long userId) {
        TypedQuery<BookMarks> query = entityManager.createQuery("FROM bookmarks WHERE user.id =: userId", BookMarks.class)
                .setParameter("userId", userId);
        return SingleResultUtil.getSingleResultOrNull(query);
    }

    @Override
    public Optional<BookMarks> getBookmarkByQuestionId(Long questionId, Long userId) {

        TypedQuery<BookMarks> query = entityManager.createQuery(
                "from BookMarks where question.id = :qId and user.id = :uId", BookMarks.class)
                .setParameter("qId", questionId)
                .setParameter("uId", userId);

        return SingleResultUtil.getSingleResultOrNull(query);
    }

}
