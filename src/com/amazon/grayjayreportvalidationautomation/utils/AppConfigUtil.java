package com.amazon.grayjayreportvalidationautomation.utils;

import amazon.platform.config.AppConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Vector;

public class AppConfigUtil {
    public static final Logger LOGGER = LogManager.getLogger(AppConfigUtil.class);

    private static final String R2MS_DOMAIN_KEY = "GrayJayReportValidationAutomation.R2MS_DOMAIN";
    private static final String REALM_KEY = "GrayJayReportValidationAutomation.REALM";
    private static final String REGION_KEY = "GrayJayReportValidationAutomation.REGION";
    private static final String QUEUE_NAME_KEY = "GrayJayReportValidationAutomation.SQS_NAME";
    private static final String DB_USER_KEY= "GrayJayReportValidationAutomation.DB_USER";
    private static final String DB_NAME_KEY  = "GrayJayReportValidationAutomation.DB_NAME";
    private static final String CLUSTER_NAME_KEY = "GrayJayReportValidationAutomation.CLUSTER_NAME";
    private static final String DB_ROLE_ARN_KEY= "GrayJayReportValidationAutomation.DB_ROLE_ARN";
    private static final String DB_ROLE_SESSION_NAME_KEY = "GrayJayReportValidationAutomation.DB_ROLE_SESSION_NAME";
    private static final String DATE_PATTERN_KEY = "GrayJayReportValidationAutomation.DATE_PATTERN";
    private static final String APPLICATION_TIME_ZONE_KEY = "GrayJayReportValidationAutomation.APPLICATION_TIME_ZONE";
    private static final String GRAY_JAY_VENDORS_KEY = "GrayJayReportValidationAutomation.GrayJayVendors";

    public String getR2MSDomain() { return AppConfig.findString(R2MS_DOMAIN_KEY); }

    public String getRegion() { return AppConfig.findString(REGION_KEY); }
    public String getRealm() { return AppConfig.findString(REALM_KEY); }

    public String getQueueName() {
        return AppConfig.findString(QUEUE_NAME_KEY);
    }

    public String getDBUser() {return AppConfig.findString(DB_USER_KEY); }

    public String getDBName() {return AppConfig.findString(DB_NAME_KEY); }

    public String getDBCluster() {return AppConfig.findString(CLUSTER_NAME_KEY); }

    public String getDBRoleARN() {return AppConfig.findString(DB_ROLE_ARN_KEY); }

    public String getDBRoleSession() {return AppConfig.findString(DB_ROLE_SESSION_NAME_KEY); }

    public String getDatePattern() {return AppConfig.findString(DATE_PATTERN_KEY); }

    public String getTimeZone() {return AppConfig.findString(APPLICATION_TIME_ZONE_KEY); }

    public Vector getGrayJayVendors() {return AppConfig.findVector(GRAY_JAY_VENDORS_KEY);}
}
