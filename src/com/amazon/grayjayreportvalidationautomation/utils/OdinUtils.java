package com.amazon.grayjayreportvalidationautomation.utils;

import com.amazon.grayjayreportvalidationautomation.Odin.OdinEncryptionMaterialsProvider;
import com.amazon.grayjayreportvalidationautomation.Odin.OdinMaterialFetcher;
import com.amazon.grayjayreportvalidationautomation.definition.MaterialSetKey;
import com.amazon.odin.everywhere.OdinMaterialRetriever;
import com.amazon.odin.everywhere.model.Material;
import com.amazon.odin.everywhere.model.MaterialPair;
import com.amazon.odin.everywhere.OdinMaterialRetriever;
import com.amazon.odin.everywhere.model.Material;
import com.amazon.odin.everywhere.model.MaterialPair;
import com.amazon.odin.everywhere.model.MaterialType;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import com.amazon.odin.everywhere.model.MaterialType;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class OdinUtils {

    public static final Logger LOGGER = LogManager.getLogger(OdinUtils.class);

    public OdinEncryptionMaterialsProvider getOdinEncryptionMaterialsProvider(String materialSet) throws Exception {
        HashMap<String, byte[]> creds = getCredentials(materialSet);
        LOGGER.info("Got Credentials from Odin: " + creds);
        PublicKey publicKey = getPublicKey(creds);
        LOGGER.info("Got Public Key from Odin: " +publicKey);
        PrivateKey privateKey = getPrivateKey(creds);
        LOGGER.info("Got Private Key from Odin: " + privateKey);
        OdinEncryptionMaterialsProvider oemp = new OdinEncryptionMaterialsProvider(
                new EncryptionMaterials(createKeyPair(publicKey, privateKey)),
                materialSet,
                1 );
        return oemp;
    }

    private KeyPair createKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        return new KeyPair(publicKey, privateKey);
    }

    private PublicKey getPublicKey(HashMap<String, byte[]> credential) throws Exception {
        return getKeyFactoryRSAInstance()
                .generatePublic(
                        new X509EncodedKeySpec(
                                credential.get("Principal")));
    }

    private PrivateKey getPrivateKey(HashMap<String, byte[]> credential) throws Exception {
        return getKeyFactoryRSAInstance()
                .generatePrivate(
                        new PKCS8EncodedKeySpec(
                                credential.get("Credential")));
    }

    private KeyFactory getKeyFactoryRSAInstance() throws Exception {
        return KeyFactory.getInstance("RSA");
    }

    private HashMap getCredentials(String materialSet) throws Exception {
        HashMap<String, byte[]> credentialsMap = new HashMap<String, byte[]>();
        OdinMaterialRetriever retriever = new OdinMaterialRetriever();
        byte[] principal = getPrincipal(materialSet, retriever);
        byte[] credential = getCredential(materialSet, retriever);
        credentialsMap.put("Principal", principal);
        credentialsMap.put("Credential", credential);
        return credentialsMap;
    }

    private byte[] getPrincipal(String materialSet, OdinMaterialRetriever retriever) throws Exception {
        try {
            MaterialPair pair = retriever.retrievePair(materialSet);
            byte[] principal = pair.getPublicMaterial().getMaterialData();
            return principal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getCredential(String materialSet, OdinMaterialRetriever retriever) throws Exception {
        try {
            MaterialPair pair = retriever.retrievePair(materialSet);
            byte[] credential = pair.getPrivateMaterial().getMaterialData();
            return credential;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static SecretKey getSymmetricKeyFromOdin(OdinMaterialRetriever odin, String keyName) throws Exception {
        Material material = odin.retrieve(keyName, MaterialType.SymmetricKey);

        byte[] materialData = material.getMaterialData();
        String algoType = material.getAlgorithm();

        SecretKey newKey = new SecretKeySpec(materialData, algoType);
        return newKey;
    }

    /**
     * @param keyName Odin material set name
     * @return secret key retrieved from Odin.
     */
    public SecretKey getSymmetricKey(String keyName) throws Exception {
        return getSymmetricKeyFromOdin(new OdinMaterialRetriever(), keyName);
    }


    public AmazonS3 createS3ClientForOdin() {
        final String s3Region = "us-east-1";
        final String encryptionMaterialSet = "com.amazon.dpen.test.paymentStatusChangeEvent.S3EncryptionKeyPair";
        final MaterialSetKey materialSetKey =
                MaterialSetKey.builder().materialSet(encryptionMaterialSet).serial(1L).build();

        final EncryptionMaterials encryptionMaterials =
                new EncryptionMaterials(constructKeyPair(materialSetKey));
        encryptionMaterials.addDescription("mat-set", materialSetKey.getMaterialSet());
        encryptionMaterials.addDescription("mat-serial",
                String.valueOf(materialSetKey.getSerial()));
        encryptionMaterials.addDescription("mat-type", "PublicKey");

        final StaticEncryptionMaterialsProvider encryptionMaterialsProvider =
                new StaticEncryptionMaterialsProvider(encryptionMaterials);

        AmazonS3 amazonS3 = AmazonS3EncryptionClientBuilder.standard()
                .withEncryptionMaterials(encryptionMaterialsProvider)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(Regions.fromName(s3Region)).build();

        return amazonS3;

    }

    public KeyPair constructKeyPair(final MaterialSetKey materialSetKey) {
        final OdinMaterialFetcher odinMaterialFetcher = new OdinMaterialFetcher();
        final Material publicKeyMaterial =
                odinMaterialFetcher.getMaterialFromOdin(materialSetKey.getMaterialSet(),
                        MaterialType.PublicKey, materialSetKey.getSerial());
        final Material privateKeyMaterial =
                odinMaterialFetcher.getMaterialFromOdin(materialSetKey.getMaterialSet(),
                        MaterialType.PrivateKey, materialSetKey.getSerial());
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PublicKey publicKey = keyFactory
                    .generatePublic(new X509EncodedKeySpec(publicKeyMaterial.getMaterialData()));
            final PrivateKey privateKey = keyFactory
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyMaterial.getMaterialData()));
            return new KeyPair(publicKey, privateKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

}
