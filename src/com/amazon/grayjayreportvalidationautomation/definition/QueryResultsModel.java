package com.amazon.grayjayreportvalidationautomation.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class QueryResultsModel {
    private Double invoice_amount;
    private String vendor_code;
    private String disbursement_type;
    private String disbursement_id;
    private String tracking_id;
    private String payment_start_date;
    private String payment_end_date;
    private String caspian_paymentstatuschangeevent_s3_bucket;
    private String caspian_paymentstatuschangeevent_s3_key;
    private double commission_on_province;

}
