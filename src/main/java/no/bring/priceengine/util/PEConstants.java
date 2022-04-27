package no.bring.priceengine.util;

public final class PEConstants  {

	  public static final boolean PASSES = true;

	  public static final boolean FAILS = false;
	  
	  public static final boolean SUCCESS = true;
	  
	  public static final boolean FAILURE = false;

	  public static final int NOT_FOUND = -1;
	  
	  /** System property - <tt>line.separator</tt>*/
	  public static final String NEW_LINE = System.getProperty("line.separator");
	  /** System property - <tt>file.separator</tt>*/
	  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	  /** System property - <tt>path.separator</tt>*/
	  public static final String PATH_SEPARATOR = System.getProperty("path.separator");
	  
	  public static final String EMPTY_STRING = "";
	  public static final String SPACE = " ";
	  public static final String TAB = "\t";
	  public static final String SINGLE_QUOTE = "'";
	  public static final String PERIOD = ".";
	  public static final String DOUBLE_QUOTE = "\"";

	  
	  public static final Integer DISCOUNT_STATUS_ACTIVE = new Integer("1");
	  public static final String  TEST_KUNDE_IGNORED = new String("1964");
	  
	  
	  public static final Integer CUSTOMPRICE_SPEC_TP_CD_CUSTOM_PRICE_MATRIX = new Integer("3");
	  public static final Integer CUSTOMPRICE_SPEC_TP_CD_CUSTOM_PRICE_DIRECT = new Integer("1");


	  public static final String DISCOUNT_CALC_TP_NAME_PROSENT =   "Prosent";
	  public static final String DISCOUNT_CALC_TP_NAME_BELOP =   "Beløp";
	  public static final String DISCOUNT_CALC_TP_NAME_NY_PRIS =   "Ny pris";
	  

	  public static final Integer DISCOUNT_CALC_TP_CD_PROSENT =   new Integer("1");
	  public static final Integer DISCOUNT_CALC_TP_CD_BELOP =   new Integer("2");
	  public static final Integer DISCOUNT_CALC_TP_CD_NY_PRIS =   new Integer("3");
	  public static final Integer DISCOUNT_CALC_TP_CD_GRATIS =   new Integer("4");

	  
	  public static final Integer CONTRACT_COMPONENT_TP_CD_MAIN =   new Integer("1");
	  public static final Integer CONTRACT_COMPONENT_TP_CD_SUB =   new Integer("2");
	  
	  
	  public static final Integer CONTRACT_ROLE_TP_CD_SIGNER =   new Integer("1");
	  public static final Integer CONTRACT_ROLE_TP_CD_USER =   new Integer("2");
	  
	  


	  
	  

	  
		
	  
	  // PRIVATE //

	  /**
	   The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
	   and so on. Thus, the caller should be prevented from constructing objects of 
	   this class, by declaring this private constructor. 
	  */
	  private PEConstants(){
	    //this prevents even the native class from 
	    //calling this ctor as well :
	    throw new AssertionError();
	  }
	}