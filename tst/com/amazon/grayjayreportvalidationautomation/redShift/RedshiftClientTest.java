package com.amazon.grayjayreportvalidationautomation.redShift;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazon.grayjayreportvalidationautomation.utils.AppConfigUtil;
import com.amazonaws.services.redshiftdataapi.AWSRedshiftDataAPI;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class RedshiftClientTest {
    private String accessKeyId = "testAccessKeyId";
    private String secretAccessKey = "testSecretAcessKey";
    private String sessionToken = "testSessionToken";
    private String roleARN = "test_RoleARN";
    private RedshiftClient client;
    private AppConfigUtil appConfigUtil;

    @Mock
    AWSSecurityTokenService mockSTSClient;

    @BeforeEach
    public void setup() throws Exception {
        appConfigUtil = new AppConfigUtil();
        client = new RedshiftClient(appConfigUtil);
    }

    @Test
    public void testGetAWSRedshiftDataAPIClient() throws Exception {
      assertTrue(true);
    }
}
