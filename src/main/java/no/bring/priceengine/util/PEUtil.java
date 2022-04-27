package no.bring.priceengine.util;
import java.math.BigDecimal;


public class PEUtil {

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);


	
	
	
	
	public static BigDecimal percentage(BigDecimal base, BigDecimal pct){
	    return base.multiply(pct).divide(ONE_HUNDRED);
	}
	


}
