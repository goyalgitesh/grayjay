package com.amazon.grayjayreportvalidationautomation.redShift;

import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.redshiftdataapi.AWSRedshiftDataAPI;
import com.amazonaws.services.redshiftdataapi.AWSRedshiftDataAPIClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerRegistry;

import javax.inject.Inject;
import java.util.Properties;

public class RedshiftClient {
    AppConfigUtil appConfigUtil;

    @Inject
    public RedshiftClient(AppConfigUtil appConfigUtil) {
        this.appConfigUtil = appConfigUtil;
    }


    public static final Logger LOGGER = LogManager.getLogger(RedshiftClient.class);

    public  AWSRedshiftDataAPI getAWSRedshiftDataAPIClient() throws Exception {

        final Properties properties = new Properties();
        properties.setProperty("DbUser", "gdrs_user");
        properties.setProperty("useSSL", "true");
        properties.setProperty("requireSSL", "true");
        properties.setProperty("verifyServerCertificate", "false");
        properties.setProperty("AccessKeyID", getTemporaryCredentials().getAWSAccessKeyId());
        properties.setProperty("SecretAccessKey", getTemporaryCredentials().getAWSAccessKeyId());

        LOGGER.info("RedShiftDataAPI client build start");
        AWSRedshiftDataAPI client = AWSRedshiftDataAPIClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(getTemporaryCredentials()))
                .withRegion("us-east-1")
                .build();
        LOGGER.info("RedShiftDataAPI Client Build Successfully -> " + client);

        return client;

    }


    public AWSCredentials getTemporaryCredentials() throws Exception {

        LOGGER.info("Region -> " + appConfigUtil.getRegion());
        LOGGER.info("Rolearn -> " + appConfigUtil.getDBRoleARN());
        LOGGER.info("RoleSession -> " + appConfigUtil.getDBRoleSession());
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(appConfigUtil.getRegion())
                .build();
        LOGGER.info("STSClient for QueryHandler created " + stsClient);
        AssumeRoleRequest queryHandlerRoleRequest = new AssumeRoleRequest()
                .withRoleArn("arn:aws:iam::205526357709:role/CaspianQueryHandlerForTestBeta")
                .withRoleSessionName("gdrs")
                .withDurationSeconds(3600);
        LOGGER.info("queryHandlerRoleRequest build " + queryHandlerRoleRequest);
        AssumeRoleResult queryHandlerAssumeResult = stsClient.assumeRole(queryHandlerRoleRequest);
        LOGGER.info("QueryHandler Assumed role successfully");
        Credentials queryHandlerTemporaryCredentials = queryHandlerAssumeResult.getCredentials();
        LOGGER.info("QueryHandler Temporary Credentials fetched");


        LOGGER.info("ACCESS_KEY_ID ===> " + queryHandlerTemporaryCredentials.getAccessKeyId());
        LOGGER.info("SECRET_ACCESS_KEY ===> " + queryHandlerTemporaryCredentials.getSecretAccessKey());
        LOGGER.info("SESSION_TOKEN ===> " + queryHandlerTemporaryCredentials.getSessionToken());


//        System.setProperty("AWS_ACCESS_KEY_ID",queryHandlerTemporaryCredentials.getAccessKeyId());
//        System.setProperty("AWS_SECRET_ACCESS_KEY",queryHandlerTemporaryCredentials.getSecretAccessKey());
//        LOGGER.info("ACCESS KEY and SECRET ACCESS KEY set from queryHandler Assumed role");

        AWSCredentials queryHandlerAWSCredentials = new BasicSessionCredentials(queryHandlerTemporaryCredentials.getAccessKeyId(),
               queryHandlerTemporaryCredentials.getSecretAccessKey(),
               queryHandlerTemporaryCredentials.getSessionToken());
        LOGGER.info("QueryHandlerAWS Credentials: " + queryHandlerAWSCredentials);
        AWSStaticCredentialsProvider queryHandlerAWSCredentialsProvider =
                new AWSStaticCredentialsProvider(queryHandlerAWSCredentials);

        AWSSecurityTokenService stsClientForRedshift;
           stsClientForRedshift = AWSSecurityTokenServiceClientBuilder.standard()
                   .withRegion(appConfigUtil.getRegion())
                   .withCredentials(queryHandlerAWSCredentialsProvider)
                   .build();
           LOGGER.info("STS Client For redshift build " + stsClientForRedshift);

        AssumeRoleRequest redshiftRoleRequest = new AssumeRoleRequest()
                .withRoleArn("arn:aws:iam::941837803842:role/RedShiftIAMCredentialRoleFor-gdrs_user-beta-USAmazon")
                .withRoleSessionName("gdrs")
                .withDurationSeconds(3600);
        LOGGER.info("RedshiftRole Request Done " + redshiftRoleRequest);
        AssumeRoleResult redshiftAssumeResult = stsClientForRedshift.assumeRole(redshiftRoleRequest);
        LOGGER.info("RedshiftAssume result Done " + redshiftAssumeResult);
        Credentials temporaryCredentials = redshiftAssumeResult.getCredentials();
        LOGGER.info("Redshift credentials fetched");

        LOGGER.info("Redshift ACCESS_KEY_ID ===> " + temporaryCredentials.getAccessKeyId());
        LOGGER.info("Redshift SECRET_ACCESS_KEY ===> " + temporaryCredentials.getSecretAccessKey());
        LOGGER.info("Redshift SESSION_TOKEN ===> " + temporaryCredentials.getSessionToken());
        AWSCredentials dbCredentials = new BasicSessionCredentials(temporaryCredentials.getAccessKeyId(),
                temporaryCredentials.getSecretAccessKey(),
                temporaryCredentials.getSessionToken());
        LOGGER.info("Successfully fetch awsCredentials");

        return dbCredentials;

    }

    @VisibleForTesting
    AWSCredentials getRedshiftCredentialsTest() throws Exception {
        return getTemporaryCredentials();
    }
}
