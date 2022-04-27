package no.bring.priceengine.util;

public class ContractPriceProcesingUtils {

    enum  ContractProcessing {
        PURE_COUNTRY_CODE,
        COUNTRY_CODE_FULL_POSTAL_CODDE,
        COUNTRY_CODE_PARTIAL_POSTAL_CODE,
        STANDARD_ZONE_TABLE,
        CUSTOM_ZONE,
        STANDARD_ZONE,
        SURCHARE_EXEMPTION,
        COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE
    }
}
