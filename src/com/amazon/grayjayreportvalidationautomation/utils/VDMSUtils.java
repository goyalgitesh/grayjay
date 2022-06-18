package com.amazon.grayjayreportvalidationautomation.utils;

import com.amazon.grayjayreportvalidationautomation.definition.ApplicationConstants;
import com.amazon.grayjayreportvalidationautomation.exceptions.ExecuteQueryException;
import com.amazon.grayjayreportvalidationautomation.redShift.DBQuery;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftClient;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftResultsMapper;
import com.amazonaws.services.redshiftdataapi.AWSRedshiftDataAPI;
import com.amazonaws.services.redshiftdataapi.model.GetStatementResultResult;
import com.amazonaws.services.redshiftdataapi.model.GetStatementResultRequest;
import com.amazonaws.services.redshiftdataapi.model.ExecuteStatementResult;
import com.amazonaws.services.redshiftdataapi.model.ExecuteStatementRequest;
import com.amazonaws.services.redshiftdataapi.model.ExecuteStatementException;
import com.amazonaws.services.redshiftdataapi.model.DescribeStatementRequest;
import com.amazonaws.services.redshiftdataapi.model.DescribeStatementResult;
import com.amazonaws.services.redshiftdataapi.model.Field;
import com.amazonaws.services.redshiftdataapi.model.ColumnMetadata;
import com.amazonaws.services.redshiftdataapi.model.CancelStatementRequest;
import com.amazonaws.services.redshiftdataapi.model.CancelStatementResult;
import com.amazonaws.util.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.stream.Collectors;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import static com.amazonaws.services.redshiftdataapi.model.StatusString.FINISHED;
import static com.amazonaws.services.redshiftdataapi.model.StatusString.ABORTED;
import static com.amazonaws.services.redshiftdataapi.model.StatusString.FAILED;

public class VDMSUtils<T> {
    AppConfigUtil appConfigUtil;
    CalendarUtil calendarUtil;
    AWSRedshiftDataAPI client;

    @Inject
    public VDMSUtils(AppConfigUtil appConfigUtil, CalendarUtil calendarUtil,RedshiftClient redshiftClient) throws Exception {
        this.appConfigUtil = appConfigUtil;
        this.calendarUtil = calendarUtil;
        this.client = redshiftClient.getAWSRedshiftDataAPIClient();
    }

    public static final Logger LOGGER = LogManager.getLogger(VDMSUtils.class);


    public List<T> run(RedshiftResultsMapper<T> mapper, String sql, int timeout)
            throws ExecuteQueryException, InterruptedException {

        String queryId = runQuery("caspianeradarnabeta","gdrs_user"
                ,"caspian-eradar-beta-redshift-us-east-1" , sql, timeout);
        return getResults(queryId, mapper);

    }

    private String runQuery(String dbName, String dbUser, String clusterId, String sql, int timeout)
            throws ExecuteQueryException, InterruptedException {
        ExecuteStatementRequest executeStatementRequest = new ExecuteStatementRequest().withDatabase(dbName)
                .withClusterIdentifier(clusterId).withDbUser(dbUser).withSql(sql);

        ExecuteStatementResult response = this.client.executeStatement(executeStatementRequest);
        String queryId = response.getId();

        LOGGER.info("ExecuteStatement started for queryId : {}, query:{}", queryId, sql);
        long startTime = System.currentTimeMillis();
        while (true) {
            DescribeStatementResult describeStatementResult = describeStatement(queryId);
            String status = describeStatementResult.getStatus();

            if (status.equals(FINISHED.toString())) {
                LOGGER.info("ExecuteStatement finished for query id : {}", queryId);
                return queryId;
            } else if (status.equals(FAILED.toString())) {
                LOGGER.error("ExecuteStatement failed");
                throw new ExecuteQueryException(describeStatementResult.getError());
            } else if (status.equals(ABORTED.toString())) {
                LOGGER.error("User Canceled Request, ExecuteStatement Aborted");
                throw new ExecuteQueryException(describeStatementResult.getError());
            } else if (System.currentTimeMillis() - startTime > timeout) {
                LOGGER.error("ExecuteStatement timed out, canceling query...");
                cancelStatement(queryId);
                throw new ExecuteQueryException("query Time out");
            } else {
                Thread.sleep(ApplicationConstants.QUERY_EXECUTION_INTERVAL_MS);
            }
        }
    }

    private List<T> getResults(String queryId, RedshiftResultsMapper<T> mapper) {
        List<List<Field>> records = new ArrayList<>();
        List<ColumnMetadata> columnMetadata = new ArrayList<>();
        String nextToken = "";

        LOGGER.info("Getting all results...");
        do {
            GetStatementResultRequest request = new GetStatementResultRequest().withId(queryId);
            GetStatementResultResult result = this.client.getStatementResult(request);
            records.addAll(result.getRecords());
            nextToken = result.getNextToken();
            if (columnMetadata.isEmpty()) {
                columnMetadata = result.getColumnMetadata();
            }
        } while (!StringUtils.isNullOrEmpty(nextToken));
        LOGGER.info("Start mapping results...");
        List<T> results = mapResults(records, columnMetadata, mapper);

        return results;
    }
    private void cancelStatement(String queryId) {
        CancelStatementRequest cancelStatementRequest = new CancelStatementRequest().withId(queryId);
        CancelStatementResult result = this.client.cancelStatement(cancelStatementRequest);
        LOGGER.info(result.getStatus());
    }

    private DescribeStatementResult describeStatement(String queryId) {
        DescribeStatementRequest request = new DescribeStatementRequest().withId(queryId);
        DescribeStatementResult describeResponse = this.client.describeStatement(request);

        return describeResponse;
    }

    private List<T> mapResults(List<List<Field>> records, List<ColumnMetadata> columnMetadata,
                               RedshiftResultsMapper<T> mapper) {
        return records.stream().map(row -> mapper.mapRecordToObject(row, columnMetadata)).collect(Collectors.toList());
    }

}
