package com.amazon.grayjayreportvalidationautomation.r2ms;

import com.amazon.cloudauth.client.CloudAuthCredentials;
import com.amazon.coral.client.ClientBuilder;
import com.amazon.coral.client.cloudauth.CloudAuthDefaultCredentialsVisitor;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftClient;
import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.amazon.r2ms.RoyaltyReportingManagementServiceClient;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class R2MSClient {
    AppConfigUtil appConfigUtil;

    public R2MSClient(AppConfigUtil appConfigUtil) {
        this.appConfigUtil = appConfigUtil;
    }
    public static final Logger LOGGER = LogManager.getLogger(R2MSClient.class);



    public RoyaltyReportingManagementServiceClient getR2MSClient() {
        RoyaltyReportingManagementServiceClient r2MSClient =  new ClientBuilder().
                remoteOf(RoyaltyReportingManagementServiceClient.class)
                .withConfiguration(appConfigUtil.getR2MSDomain() + "." + appConfigUtil.getRegion())
                .withCallVisitors(new CloudAuthDefaultCredentialsVisitor(new CloudAuthCredentials.RegionalAwsCredentials(
                        DefaultAWSCredentialsProviderChain.getInstance(), appConfigUtil.getRegion())))
                .newClient();

        return r2MSClient;
    }

}
