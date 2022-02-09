package com.javamentor.qa.platform.dao.impl.dto.transformers;

import com.javamentor.qa.platform.models.dto.ReputationHistoryDtoList;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import org.hibernate.transform.ResultTransformer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReputationHistoryDtoListTransformer implements ResultTransformer {

    private Map<Long, ReputationHistoryDtoList> reputationHistoryDtoListMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {

        Map<String, Integer> aliasToIndexMapTrans = AliasToIndexMap.get(aliases);
        Long reputationHistoryId = ((Number) tuple[0]).longValue();

        return reputationHistoryDtoListMap.computeIfAbsent(
                reputationHistoryId,
                id1 -> {
                    ReputationHistoryDtoList reputationHistoryDtoListTemp = new ReputationHistoryDtoList();
                    reputationHistoryDtoListTemp.setId(((Number) tuple[aliasToIndexMapTrans.get("reputation_id")]).longValue());
                    reputationHistoryDtoListTemp.setCount(((Number) tuple[aliasToIndexMapTrans.get("reputation_count")]).intValue());
                    reputationHistoryDtoListTemp.setType(((ReputationType) tuple[aliasToIndexMapTrans.get("reputation_type")]));
                    reputationHistoryDtoListTemp.setPersistDate(((LocalDateTime) tuple[aliasToIndexMapTrans.get("reputation_persistDate")]));

                    return  reputationHistoryDtoListTemp;
                }
        );
    }

    @Override
    public List transformList(List list) {
        return new ArrayList<>(reputationHistoryDtoListMap.values());
    }
}
