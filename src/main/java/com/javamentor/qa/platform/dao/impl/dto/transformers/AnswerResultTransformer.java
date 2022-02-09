package com.javamentor.qa.platform.dao.impl.dto.transformers;

import com.javamentor.qa.platform.models.dto.AnswerDto;
import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;
import org.hibernate.transform.ResultTransformer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnswerResultTransformer implements ResultTransformer {

    private Map<Long, AnswerDto> answerDtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {

        Map<String, Integer> aliasToIndexMapTrans = AliasToIndexMap.get(aliases);
        Long answerId = ((Number) tuple[0]).longValue();

        AnswerDto answerDto = answerDtoMap.computeIfAbsent(
                answerId,
                id -> {
                    AnswerDto answerDtoTemp = new AnswerDto();
                    answerDtoTemp.setId(((Number) tuple[aliasToIndexMapTrans.get("answer_id")]).longValue());
                    answerDtoTemp.setUserId(((Number) tuple[aliasToIndexMapTrans.get("user_id")]).longValue());
                    answerDtoTemp.setQuestionId(((Number) tuple[aliasToIndexMapTrans.get("question_id")]).longValue());
                    answerDtoTemp.setBody((String) tuple[aliasToIndexMapTrans.get("html_body")]);
                    answerDtoTemp.setPersistDate((LocalDateTime) tuple[aliasToIndexMapTrans.get("persist_date")]);
                    answerDtoTemp.setIsHelpful((Boolean) tuple[aliasToIndexMapTrans.get("is_helpful")]);
                    answerDtoTemp.setDateAccept((LocalDateTime) tuple[aliasToIndexMapTrans.get("date_accept_time")]);
                    answerDtoTemp.setCountValuable(((Number) tuple[aliasToIndexMapTrans.get("answer_countValuable")]).longValue());
                    answerDtoTemp.setImage((String) tuple[aliasToIndexMapTrans.get("image_link")]);
                    answerDtoTemp.setNickName((String) tuple[aliasToIndexMapTrans.get("fullName")]);

                    return answerDtoTemp;
                }
        );
        return answerDto;
    }

    @Override
    public List transformList(List list) {
        return new ArrayList<>(answerDtoMap.values());
    }
}
