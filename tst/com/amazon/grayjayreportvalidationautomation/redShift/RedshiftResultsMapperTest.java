package com.amazon.grayjayreportvalidationautomation.redShift;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.amazon.grayjayreportvalidationautomation.definition.QueryResultsModel;
import com.amazonaws.services.redshiftdataapi.model.ColumnMetadata;
import com.amazonaws.services.redshiftdataapi.model.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class RedshiftResultsMapperTest {
    private RedshiftResultsMapper<QueryResultsModel> resultsMapper;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
        resultsMapper = new RedshiftResultsMapper<>(QueryResultsModel.class);
    }

    @Test
    public void testRedshiftResultsMapperHappyPath(){
        List<Field> record = Arrays.asList(new Field().withStringValue("testDisbursement"));
        List<ColumnMetadata> columnMetadata = Arrays.asList(new ColumnMetadata().withName("disbursement_id"));
        QueryResultsModel mapperExpected = QueryResultsModel.builder().disbursement_id("testDisbursement").build();
        QueryResultsModel mapperResult = resultsMapper.mapRecordToObject(record, columnMetadata);
        assertEquals(mapperExpected, mapperResult);
    }

    @Test
    public void testGetFieldValueBlob(){
        ByteBuffer TEST_BLOB = ByteBuffer.wrap("testBlob".getBytes());
        Field field = new Field().withBlobValue(TEST_BLOB);
        assertEquals(resultsMapper.getFieldValueTest(field), TEST_BLOB);
    }

    @Test
    public void testGetFieldValueBoolean(){
        Field field = new Field().withBooleanValue(true);
        assertEquals(resultsMapper.getFieldValueTest(field), true);
    }

    @Test
    public void testGetFieldValueDouble(){
        Field field = new Field().withDoubleValue(0.123456);
        assertEquals(resultsMapper.getFieldValueTest(field), 0.123456);
    }

    @Test
    public void testGetFieldValueLong(){
        Field field = new Field().withLongValue(123456L);
        assertEquals(resultsMapper.getFieldValueTest(field), 123456L);
    }

    @Test
    public void testGetFieldValueString(){
        Field field = new Field().withStringValue("TEST_STRING");
        assertEquals(resultsMapper.getFieldValueTest(field), "TEST_STRING");
    }
}