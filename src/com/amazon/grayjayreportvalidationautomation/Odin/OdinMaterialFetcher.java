package com.amazon.grayjayreportvalidationautomation.Odin;


import java.nio.charset.StandardCharsets;

import com.amazon.odin.everywhere.OdinMaterialRetriever;
import com.amazon.odin.everywhere.model.BasicAWSCredentialsProvider;
import com.amazon.odin.everywhere.model.Material;
import com.amazon.odin.everywhere.model.MaterialPair;
import com.amazon.odin.everywhere.model.MaterialType;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OdinMaterialFetcher {

    public BasicAWSCredentialsProvider getCredentialsFromOdin(final String odinMaterialSet) {
        try {
            final OdinMaterialRetriever odinMaterialRetriever = new OdinMaterialRetriever();
            final MaterialPair credentialsPair =
                    odinMaterialRetriever.retrievePair(odinMaterialSet);
            String principal = new String(credentialsPair.getPublicMaterial().getMaterialData(),
                    StandardCharsets.UTF_8);
            String credential = new String(credentialsPair.getPrivateMaterial().getMaterialData(),
                    StandardCharsets.UTF_8);
            log.info("Successfully retrieved credentials for material set: " + odinMaterialSet);
            return new BasicAWSCredentialsProvider(principal, credential);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to retrieve credentials for material set: " + odinMaterialSet, ex);
        }
    }

    public Material getMaterialFromOdin(
            @NonNull final String odinMaterialSet,
            @NonNull final MaterialType materialType,
            @NonNull final long materialSerial) {
        try {
            final OdinMaterialRetriever odinMaterialRetriever = new OdinMaterialRetriever();
            return odinMaterialRetriever.retrieve(odinMaterialSet, materialType, materialSerial);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to retrieve credentials for material set: " + odinMaterialSet, e);
        }
    }
}
