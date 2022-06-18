package com.amazon.grayjayreportvalidationautomation.handler;

import amazon.platform.config.AppConfig;
import com.amazon.grayjayreportvalidationautomation.application.UtilsInjector;
import com.amazon.grayjayreportvalidationautomation.definition.MaterialSetKey;
import com.amazon.grayjayreportvalidationautomation.definition.QueryResultsModel;
import com.amazon.grayjayreportvalidationautomation.definition.ReportModel;
import com.amazon.grayjayreportvalidationautomation.r2ms.R2MSClient;
import com.amazon.grayjayreportvalidationautomation.redShift.DBQuery;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftClient;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftResultsMapper;
import com.amazon.grayjayreportvalidationautomation.utils.R2MSUtils;
import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.amazon.grayjayreportvalidationautomation.utils.CalendarUtil;

import com.amazon.r2ms.ReportDetail;
import com.amazon.r2ms.RoyaltyReportingManagementServiceClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazon.grayjayreportvalidationautomation.definition.ApplicationConstants;
import com.amazonaws.services.redshiftdataapi.AWSRedshiftDataAPI;
import com.amazonaws.services.s3.AmazonS3;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Handler implements RequestHandler<Map<String,String>,Context> {

    String root = System.getenv(ApplicationConstants.ROOT);
    public static final Logger LOGGER = LogManager.getLogger(Handler.class);

    @SneakyThrows
    @Override
    public Context handleRequest(Map<String,String> events, Context context) {
        LOGGER.info("entered lambda function");
        setupEnv(events.get("domain"));
        String startDate = events.get("startDate");
        String endDate = events.get("endDate");

        // Initialization
        Injector injector = Guice.createInjector(new UtilsInjector());
        AppConfigUtil appConfigUtil = injector.getInstance(AppConfigUtil.class);
        R2MSUtils r2MSUtils = injector.getInstance(R2MSUtils.class);
        

        // R2MS
        Vector<String> vendors = appConfigUtil.getGrayJayVendors();
        LOGGER.info(vendors);
        ReportModel reportModel = r2MSUtils.buildSingleVendorR2MSRequest(new ReportModel(),
                vendors, startDate, endDate);
        List<ReportDetail> r2msReportsList =  r2MSUtils.getAvailableReports(reportModel);
        LOGGER.info("ReportDetail Size: " + r2msReportsList.size());

        // Caspian DB




        // PSCE

        // Validate


        return null;
    }

    private void setupEnv(String domain) {
        initializeAppConfig(domain);
        setCoralConfig();
        setTrustStoreFile();
    }

    private void initializeAppConfig(String domain) {
        if (!AppConfig.isInitialized()) {
            final String[] appConfigArgs = {
                    "--root", "/var/task",
                    "--domain", domain,
                    "--realm", "USAmazon"
            };
            AppConfig.initialize(ApplicationConstants.GRAY_JAY_LAMBDA, null, appConfigArgs);
            LOGGER.info("AppConfig Initialized");
        }
        }

    private void setCoralConfig() {
        System.setProperty("CORAL_CONFIG_PATH", root + "/coral-config");
        LOGGER.info("CORAL_CONFIG_PATH set successfully");
    }

    private void setTrustStoreFile() {
        System.setProperty("javax.net.ssl.trustStore", root + ApplicationConstants.TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", ApplicationConstants.TRUSTSTORE_PW);
        LOGGER.info("Truststore set successfully");
    }

}
