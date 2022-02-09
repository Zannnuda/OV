package com.javamentor.qa.platform.dao.impl.dto.transformers;

import com.javamentor.qa.platform.models.dto.QuestionDtoPrincipal;
import org.hibernate.transform.ResultTransformer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestionForPricipalResultTransformer implements ResultTransformer {

    private Map<Long, QuestionDtoPrincipal> questionDtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {

        Map<String, Integer> aliasToIndexMapTrans = AliasToIndexMap.get(aliases);
        Long questionId = ((Number) tuple[0]).longValue();

        QuestionDtoPrincipal questionDtoPrincipal = questionDtoMap.computeIfAbsent(
                questionId,
                id -> {
                    QuestionDtoPrincipal questionDtoPrincipalTemp = new QuestionDtoPrincipal();
                    questionDtoPrincipalTemp.setId(((Number) tuple[aliasToIndexMapTrans.get("question_id")]).longValue());
                    questionDtoPrincipalTemp.setUserId(((Number) tuple[aliasToIndexMapTrans.get("user_id")]).longValue());
                    questionDtoPrincipalTemp.setTitle((String) tuple[aliasToIndexMapTrans.get("question_title")]);
                    questionDtoPrincipalTemp.setPersistDate((LocalDateTime) tuple[aliasToIndexMapTrans.get("persist_date")]);
                    questionDtoPrincipalTemp.setDescription((String) tuple[aliasToIndexMapTrans.get("question_description")]);
                    questionDtoPrincipalTemp.setCountValuable(((Number) tuple[aliasToIndexMapTrans.get("question_countValuable")]).longValue());

                    return questionDtoPrincipalTemp;
                }
        );
        return questionDtoPrincipal;
    }

    @Override
    public List transformList(List list) {
        return new ArrayList<>(questionDtoMap.values());
    }
}
