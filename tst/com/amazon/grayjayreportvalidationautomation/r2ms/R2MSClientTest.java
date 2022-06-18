package com.amazon.grayjayreportvalidationautomation.r2ms;

import com.amazon.grayjayreportvalidationautomation.definition.ApplicationConstants;
import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.amazon.grayjayreportvalidationautomation.utils.R2MSUtils;
import com.amazon.r2ms.RoyaltyReportingManagementServiceClient;
import amazon.platform.config.AppConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest({ApplicationConstants.class, AppConfig.class, R2MSUtils.class})

public class R2MSClientTest {

    AppConfigUtil appConfigUtil;
    @BeforeClass
    public static void setupBefore() {
        PowerMockito.mockStatic(AppConfig.class);
    }

    @Before
    public void initSetup() throws IOException {
        File pwd = new File(".");
        String baseDir = pwd.getCanonicalPath();
        if (!AppConfig.isInitialized()) {
            final String[] appConfigArgs = {
                    "--root=" + System.getenv("LAMBDA_TASK_ROOT"),
                    "--domain=" + System.getenv("DOMAIN"),
                    "--realm=" + System.getenv("REALM")

            };
            AppConfig.initialize(ApplicationConstants.GRAY_JAY_LAMBDA, null, appConfigArgs);
        }
        AppConfig.insertString("currencyUnitsFile", baseDir + "/build/private/config/currency-units");
        appConfigUtil = new AppConfigUtil();
    }

    @Test
    public void testgetR2MSClient() throws Exception {
        String AWS_REGION = "us-east-1";
        String R2MS_DOMAIN = "beta";
        R2MSClient r2MSClient = new R2MSClient(appConfigUtil);
        PowerMockito.when(this.appConfigUtil, "getR2MSDomain").thenReturn(R2MS_DOMAIN);
        PowerMockito.when(this.appConfigUtil, "getRegion").thenReturn(AWS_REGION);
        RoyaltyReportingManagementServiceClient client = r2MSClient.getR2MSClient();
        assertThat(client, is(instanceOf(RoyaltyReportingManagementServiceClient.class)));

    }


}
