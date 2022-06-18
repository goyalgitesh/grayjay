package com.amazon.grayjayreportvalidationautomation.utils;
import com.amazon.grayjayreportvalidationautomation.r2ms.R2MSClient;
import com.amazon.grayjayreportvalidationautomation.redShift.RedshiftClient;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.KeyUnavailableException;
import com.google.inject.Inject;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.ion.IonBlob;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonType;
import software.amazon.ion.IonSystem;
import software.amazon.ion.system.IonSystemBuilder;

public class S3Util {
    AppConfigUtil appConfigUtil;
    RedshiftClient redshiftClient;
    AmazonS3 amazonS3;
    IonSystem ionSystem;

    @Inject
    public S3Util(AppConfigUtil appConfigUtil,RedshiftClient redshiftClient) throws Exception {
        this.appConfigUtil = appConfigUtil;
        this.redshiftClient = redshiftClient;
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
        this.ionSystem = IonSystemBuilder.standard().build();
    }
    public static final Logger LOGGER = LogManager.getLogger(S3Util.class);

    public CSVReader downloadFileFromS3(String s3bucket, String s3key) {

        S3Object s3Object = null;

        boolean fileExists = amazonS3.doesObjectExist(s3bucket, s3key);

        if(fileExists)
        {
            s3Object = amazonS3.getObject(new GetObjectRequest(s3bucket, s3key));
            InputStream objectData = s3Object.getObjectContent();
            CSVReader reader = new CSVReader(new InputStreamReader(objectData, StandardCharsets.UTF_8), ',', '"', 1);
            return reader;
        }
        else {
            LOGGER.info("Bucket "+s3bucket+" does not contain the file "+s3key);
            return null;
        }

    }

    private static AWSKMS getKMSClient() {

        return AWSKMSClient.builder()
                .withRegion("us-east-1")
                .build();
    }

    public List<IonStruct> fetchEventsFromChunkStore(final String bucket,
                                                     final String key,
                                                     AmazonS3 amazonS3) throws IOException {
        LOGGER.info("Fetching S3 object with bucket {}, key {}", bucket, key);
        final S3Object chunk = amazonS3.getObject(bucket, key);
        final List<IonStruct> eventStructs = new ArrayList<>();
        // Parsing S3 chunk data
        final IonReader ionReader = ionSystem.newReader(new GZIPInputStream(chunk.getObjectContent()));
        Iterator<IonValue> iterator = ionSystem.iterate(ionReader);
        while (iterator.hasNext()) {
            final IonValue event = iterator.next();
            if (!IonType.BLOB.equals(event.getType())) {
                continue;
            }

            final IonBlob ionBlob = (IonBlob)event;
            final IonStruct chunkstoreStruct = (IonStruct) ionSystem.singleValue(ionBlob.getBytes());
            eventStructs.add(chunkstoreStruct);
        }

        return eventStructs;
    }


}
