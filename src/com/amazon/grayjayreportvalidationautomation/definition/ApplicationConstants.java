package com.amazon.grayjayreportvalidationautomation.definition;

public class ApplicationConstants {

    public static final String GRAY_JAY_LAMBDA = "GrayJayReportValidationAutomation";
    public static final String REDSHIFT_URL_NA = "jdbc:redshift:iam://caspian-eradar-prod-redshift-us-east-1:us-east-1/caspianeradarna";
    public static final String ROOT = "LAMBDA_TASK_ROOT";
    public static final String REALM = "USAmazon";
    public static final String DOMAIN = "Domain";
    public static final String TRUSTSTORE = "/truststore/InternalAndExternalTrustStore.jks";
    public static final String TRUSTSTORE_PW = "amazon";
    public static final Integer QUERY_EXECUTION_INTERVAL_MS = 10000;
    public static final String PSCECOMMAND =
    "sudo -u dvs-user /apollo/env/DOWENOpsTools/bin/dowen-dumper " +
            "--creds com.amazon.access.CaspianPaymentStatusChangeEvent-DPEN-1 " +
            "--bucket dpen-na-prod-paymentstatuschangeevent --chunk";



}
