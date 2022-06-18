package com.amazon.grayjayreportvalidationautomation.definition;
import lombok.Data;

@Data

public class ReportModel {
    private Integer reportAmount;
    private String vendorCode;
    private String disbursementType;
    private String disbursementId;
    private String trackingId;
    private String startDate;
    private String endDate;

}
