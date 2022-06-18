package com.amazon.grayjayreportvalidationautomation.utils;

import com.amazon.grayjayreportvalidationautomation.definition.ReportModel;
import com.amazon.grayjayreportvalidationautomation.r2ms.R2MSClient;
import com.amazon.r2ms.ReportDetail;
import com.amazon.r2ms.RoyaltyReportingManagementServiceClient;
import com.amazon.r2ms.FindReportsRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class R2MSUtils {

    private static final Logger LOGGER = LogManager.getLogger(R2MSUtils.class);
    private final AppConfigUtil appConfigUtil;
    private final CalendarUtil calendarUtil;
    private final R2MSClient r2MSClient;
    private final RoyaltyReportingManagementServiceClient client;

    @Inject
    public R2MSUtils(AppConfigUtil appConfigUtil,CalendarUtil calendarUtil,R2MSClient r2MSClient){
        this.appConfigUtil = appConfigUtil;
        this.calendarUtil = calendarUtil;
        this.r2MSClient = r2MSClient;
        this.client = r2MSClient.getR2MSClient();
    }

    @VisibleForTesting
    public R2MSUtils(AppConfigUtil appConfigUtil, CalendarUtil calendarUtil,
                     R2MSClient r2MSClient, RoyaltyReportingManagementServiceClient serviceClient){
        this.appConfigUtil = appConfigUtil;
        this.calendarUtil = calendarUtil;
        this.r2MSClient = r2MSClient;
        this.client = serviceClient;
    }

    public List<ReportDetail> getAvailableReports(ReportModel reportModel) throws ParseException {

        List<ReportDetail> result = new ArrayList<>();
        FindReportsRequest request = FindReportsRequest.builder().build();
        request.setVendorCode(reportModel.getVendorCode());
        request.setBeginDate(calendarUtil.convertStringToDate(reportModel.getStartDate()));
        request.setEndDate(calendarUtil.convertStringToDate(reportModel.getEndDate()));


        try {
            result = findReports(this.client, request);
            if (result == null || result.size() == 0 || result.get(0).getReportId() == null) {
                LOGGER.info("No Reports Found");
            }
            else {
                LOGGER.info("Total Reports Found: " + result.size());
            }

        }
        catch (Exception e) {
            LOGGER.error("Exception occured with error : " + e);
        }

    return result;
    }

    public static List<ReportDetail> findReports(final RoyaltyReportingManagementServiceClient r2msClient,
                                                 FindReportsRequest findReportsRequest) {
        final List<ReportDetail> reportDetails = Lists.newArrayList();
        try {
            reportDetails.addAll(r2msClient.callFindReports(findReportsRequest).getReports());
        } catch (Exception e) {
            LOGGER.error("Caught exception while calling R2MS with arguments: " +
                    "; Request: " + findReportsRequest +
                    "; error: " + e);
            throw e;
        }

       LOGGER.info("Loaded " + reportDetails.size() + " reports from R2MS" +
                "; Request: " + findReportsRequest );
        return reportDetails;
    }

    public ReportModel buildSingleVendorR2MSRequest(ReportModel reportModel,
                                        Vector<String> vendors,
                                        String startDate,
                                        String endDate) {
        reportModel.setVendorCode(vendors.get(0));
        reportModel.setStartDate(startDate);
        reportModel.setEndDate(endDate);

        return reportModel;

    }
}



