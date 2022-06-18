package com.amazon.grayjayreportvalidationautomation.application;

import com.amazon.grayjayreportvalidationautomation.definition.QueryResultsModel;
import com.amazon.grayjayreportvalidationautomation.r2ms.R2MSClient;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftClient;
import com.amazon.grayjayreportvalidationautomation.utils.R2MSUtils;
import com.amazon.grayjayreportvalidationautomation.utils.PSCEUtils;
import com.amazon.grayjayreportvalidationautomation.utils.VDMSUtils;
import com.amazon.grayjayreportvalidationautomation.utils.CalendarUtil;
import com.amazon.grayjayreportvalidationautomation.utils.S3Util;
import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class UtilsInjector extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    public AppConfigUtil provideAppConfigUtil() {
        return new AppConfigUtil();
    }

    @Provides
    public CalendarUtil provideCalendarUtil(final AppConfigUtil appConfigUtil) {
        return new CalendarUtil(appConfigUtil);
    }

    @Provides
    public R2MSClient provideR2MSClient(final AppConfigUtil appConfigUtil) {
        return new R2MSClient(appConfigUtil);
    }

    @Provides
    public RedshiftClient provideRedshiftClient(final AppConfigUtil appConfigUtil) {
        return new RedshiftClient(appConfigUtil);
    }
    @Provides
    public R2MSUtils provideR2MSUtilsClient(final AppConfigUtil appConfigUtil, final CalendarUtil calendarUtil
            ,final R2MSClient r2MSClient) {
        return new R2MSUtils(appConfigUtil,calendarUtil,r2MSClient);
    }

    @Provides
    public VDMSUtils<QueryResultsModel> provideVDMSUtils(final AppConfigUtil appConfigUtil,
                                                         final CalendarUtil calendarUtil) throws Exception {
        return new VDMSUtils(appConfigUtil,calendarUtil,provideRedshiftClient(appConfigUtil));
    }
    @Provides
    public S3Util provideS3Util(final AppConfigUtil appConfigUtil) throws Exception {
        return new S3Util(appConfigUtil,provideRedshiftClient(appConfigUtil));
    }

    @Provides
    public PSCEUtils providePSCEUtils(final AppConfigUtil appConfigUtil,
                                      final CalendarUtil calendarUtil) {
        return new PSCEUtils(appConfigUtil,calendarUtil);
    }






}
