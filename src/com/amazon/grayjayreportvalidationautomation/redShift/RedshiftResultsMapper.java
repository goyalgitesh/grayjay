package com.amazon.grayjayreportvalidationautomation.redShift;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.amazonaws.services.redshiftdataapi.model.ColumnMetadata;
import com.amazonaws.services.redshiftdataapi.model.Field;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RedshiftResultsMapper<T> {
    private Class<T> recordClass;

    public T mapRecordToObject(List<Field> record, List<ColumnMetadata> metadata) {
        Map<String, Object> map = IntStream.range(0, record.size()).boxed()
                .collect(Collectors.toMap(i -> metadata.get(i).getName(), i -> getFieldValue(record.get(i))));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(map, recordClass);
    }

    private Object getFieldValue(Field item) {
        Object value = null;
        if (item.getBlobValue() != null) {
            value = item.getBlobValue();
        } else if (item.getBooleanValue() != null) {
            value = item.getBooleanValue();
        } else if (item.getDoubleValue() != null) {
            value = item.getDoubleValue();
        } else if (item.getLongValue() != null) {
            value = item.getLongValue();
        } else if (item.getStringValue() != null) {
            value = item.getStringValue();
        }
        return value;
    }

    @VisibleForTesting
    Object getFieldValueTest(Field item){
        return getFieldValue(item);
    }
}