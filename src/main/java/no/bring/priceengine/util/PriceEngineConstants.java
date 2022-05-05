package no.bring.priceengine.util;

import java.math.BigDecimal;
import java.util.Map;

import no.bring.priceengine.database.JPAUtil;

public final class PriceEngineConstants {

	// UAT
//	public static final String DATABASE_CONNECTION_URL= "jdbc:postgresql://139.114.151.175:5432/CrossborderDump_Test1?ApplicationName=PriceengineExcelToDatabase";

	// PRODUCTION
//	public static final String DATABASE_CONNECTION_URL= "jdbc:postgresql://139.114.164.201:5432/priceengine_crossborder_prod?ApplicationName=PriceengineExcelToDatabase";

	public static final Map<String,Object> PROPERTIES_MAP=JPAUtil.getEntityManagerFactory().getProperties();
    public static final String DB_CONNECTION_DRIVER = String.valueOf(PROPERTIES_MAP.get("hibernate.connection.driver_class"));
    public static final String DB_CONNECTION_URL= String.valueOf(PROPERTIES_MAP.get("javax.persistence.jdbc.url"));
    public static final String DB_CONNECTION_USERNAME="pe_admin";
    public static final String DB_CONNECTION_PASSWORD="peadmin";
	
	public static final Integer CUSTOMPRICE_SPEC_TP_CD_CUSTOM_PRICE_DIRECT = new Integer("1");
	public static final Integer CUSTOMPRICE_SPEC_TP_CD_CUSTOM_PRICE_MATRIX = new Integer("3");

	public static final Long DEFAULT_VOLUMETRIC_FACTOR = new Long("5");
	public static final Long DEFAULT_WEIGHT_1KG = new Long("1");
	
	public static final String FUEL_SURCHARGE_VAS = "3202";
	public static final String TOLL_SURCHARGE_VAS = "3201";
	public static final String INFRA_SURCHARGE_VAS = "1238";
	public static final String GLOBAL_UTILITY_SERVICE = "-9999";
	public static final Integer FUEL_SURCHARGE_B2C = new Integer("3204");
	public static final Integer FUEL_SURCHARGE_B2B_GROUPAGE = new Integer("3203");
	public static final Integer FUEL_SURCHARGE_B2B_PARCEL = new Integer("3202");
	public static final Integer TOLL_SURCHARGE = new Integer("3201");
	public static final Integer INFRA_SURCHARGE = new Integer("1238");
	
	public static final String SERVICES_B2B_PARCELS = "5000,4850,9000,9600";
	public static final String SERVICES_B2B_GROUPAGE = "5100,5200,9100";
	public static final String SERVICES_B2C_PARCELS = "5600,5800,9300";

	public static final Integer DISCOUNT_CALC_TP_CD_BELOP = new Integer("2");
	public static final Integer DISCOUNT_CALC_TP_CD_NY_PRIS = new Integer("3");
	public static final Integer DISCOUNT_CALC_TP_CD_PROSENT = new Integer("1");
	public static final Integer DISCOUNT_CALC_TP_CD_GRATIS = new Integer("4"); 

	public static final String DISCOUNT_CALC_TP_NAME_BELOP = "Beløp";
	public static final String DISCOUNT_CALC_TP_NAME_NY_PRIS = "Ny pris";
	public static final String DISCOUNT_CALC_TP_NAME_PROSENT = "Prosent";

	public static final BigDecimal HUNDRED_BIG_DECIMAL = new BigDecimal("100");

	public static final Long LONG_ZERO = new Long("0");
	public static final Long LONG_ONE = new Long("1");
	public static final long long_ONE = 1L;
	
	public static final int MINIMUM_WEIGHT_FOR_PALLETS = 161;
	/** System property - <tt>line.separator</tt> */
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final int NO_SURCHARGE = 0;
	public static final int NOT_FOUND = -1;
	public static final boolean PASSES = true;
	/** System property - <tt>path.separator</tt> */
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");
	public static final String PERIOD = ".";	
	public static final Long PRICE_TYPE_CODE_LIST_PRICE = new Long("1");
	public static final Long PRICE_TYPE_CODE_ADJUSTMENT = new Long("2");
	public static final Long PRICE_TYPE_CODE_CONTRACT_PRICE = new Long("3");
	
	public static final int PROSENT = 1;
	public static final String SINGLE_QUOTE = "'";
	public static final String SPACE = " ";
	public static final boolean SUCCESS = true;
	public static final String TAB = "\t";
	public static final String TEST_KUNDE_IGNORED = new String("1964");
	public static final BigDecimal THOUSAND_BIG_DECIMAL = new BigDecimal("1000");
	public static final String PRODUCT_TYPE_PPR_PRODUCT = "PPRProduct";
	// PRIVATE //

	/**
	 * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
	 * and so on. Thus, the caller should be prevented from constructing objects
	 * of this class, by declaring this private constructor.
	 */
	private PriceEngineConstants() {
		// this prevents even the native class from
		// calling this ctor as well :
		throw new AssertionError();
	}
}