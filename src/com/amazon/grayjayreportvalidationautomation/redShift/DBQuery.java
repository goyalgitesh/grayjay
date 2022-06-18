package com.amazon.grayjayreportvalidationautomation.redShift;

public class DBQuery {
    public final static String query = "SELECT invoice_amount,vendor_code,\n" +
            "disbursement_type,disbursement_id,tracking_id,payment_start_date,payment_end_date," +
            "caspian_paymentstatuschangeevent_s3_bucket,caspian_paymentstatuschangeevent_s3_key\n" +
            "FROM public.caspian_payment_events \n" +
            "where vendor_code in ('DPPCA','QPACA','ADPCA','PMLQQ','DQAFR','FAAFR',\n" +
            "'GMAFR','HCIES','HCAFR','HHLQQ','HSIQQ','ZDVCA','PGUQQ','PGAQQ','RHRQQ') \n" +
            "and disbursement_type = 'ManagedEBooks' AND from_status = 'PendingPaymentRedrive'\n" +
            "AND payment_start_date >= '2022-01-01' " +
            "and payment_end_date <= '2022-03-02';";

    public final static String queryBeta = "SELECT invoice_amount,vendor_code,\n" +
            "disbursement_type,disbursement_id,tracking_id,payment_start_date,payment_end_date," +
            "caspian_paymentstatuschangeevent_s3_bucket,caspian_paymentstatuschangeevent_s3_key\n" +
            "FROM public.caspian_payment_events \n" +
            "where vendor_code in ('DPPCA','QPACA','ADPCA','PMLQQ','DQAFR','FAAFR',\n" +
            "'GMAFR','HCIES','HCAFR','HHLQQ','HSIQQ','ZDVCA','PGUQQ','PGAQQ','RHRQQ') \n" +
            "and disbursement_type = 'ManagedEBooks' AND from_status = 'PendingPaymentRedrive'\n";

    public final static String testQuery = "SELECT invoice_amount,vendor_code,\n" +
            "disbursement_type,disbursement_id,tracking_id,payment_start_date,payment_end_date\n" +
            "FROM public.caspian_payment_events wHERE from_status = 'DispatchSucceeded';";
}
