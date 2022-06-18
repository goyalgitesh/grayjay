package com.amazon.grayjayreportvalidationautomation.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class PSCEModel {
    private String source;
    private Double gst = 0.0;
    private Double hst = 0.0;
    private Double qst = 0.0;
    private String tax_province;
    private String to_status;
    private String from_status;
    private String vendor_code;
    private BigInteger disbursement_id;
    private String disbursement_type;
    private String tracking_id;

}
