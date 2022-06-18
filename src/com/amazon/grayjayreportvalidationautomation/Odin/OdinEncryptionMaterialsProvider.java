package com.amazon.grayjayreportvalidationautomation.Odin;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.google.common.collect.ImmutableMap;

import java.util.Map;


public class OdinEncryptionMaterialsProvider implements EncryptionMaterialsProvider {
    private EncryptionMaterials encryptionMaterials;

    public OdinEncryptionMaterialsProvider(EncryptionMaterials encryptionMaterials, String materialSet, long serialId) {

        if (encryptionMaterials.getKeyPair() != null) {
            this.encryptionMaterials = encryptionMaterials
                    .addDescriptions(ImmutableMap.of(
                            "mat-set", materialSet,
                            "mat-serial", Long.toString(serialId),
                            "mat-type", "PublicKey"));
        } else if (encryptionMaterials.getSymmetricKey() != null) {
            this.encryptionMaterials = encryptionMaterials
                    .addDescriptions(ImmutableMap.of(
                            "mat-set", materialSet,
                            "mat-serial", Long.toString(serialId),
                            "mat-type", "SymmetricKey"
                    ));
        }
    }

    @Override
    public void refresh() { }

    @Override
    public EncryptionMaterials getEncryptionMaterials(Map<String, String> materialsDescription) {
        return encryptionMaterials;
    }

    @Override
    public EncryptionMaterials getEncryptionMaterials() {
        return encryptionMaterials;
    }
}
