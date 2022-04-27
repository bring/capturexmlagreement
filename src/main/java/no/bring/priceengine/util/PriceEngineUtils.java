package no.bring.priceengine.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static no.bring.priceengine.util.PriceEngineConstants.*;

public class PriceEngineUtils {

	/**
	 * Calculates the volumetric weight in kilograms of Consignment by dividing
	 * 'Volume of Consignment' by 'Volumetric Factor'
	 * 
	 * @param length,
	 *            width , height
	 * @return volume in decimeter cube
	 */
	public static BigDecimal calculateVolumeInDecimeterCube(Long length, Long width, Long height) {

		BigDecimal bdLength = BigDecimal.valueOf(length.longValue());
		BigDecimal bdWidth = BigDecimal.valueOf(width.longValue());
		BigDecimal bdHeight = BigDecimal.valueOf(height.longValue());

		return (bdLength.multiply(bdWidth).multiply(bdHeight)).divide(THOUSAND_BIG_DECIMAL);
	}

	/**
	 * Calculates the volumetric weight in kilograms of Consignment by dividing
	 * 'Volume of Consignment' by 'Volumetric Factor'
	 * 
	 * @param volume,
	 *            volumetricFactor
	 * @return volume in decimeter cube
	 */
	public static Long calculateVolumetricWeightInKilograms(BigDecimal volume, Long volumetricFactor) {

		BigDecimal volumetricWeight = volume.divide(BigDecimal.valueOf(volumetricFactor.longValue()));
		long longValue = volumetricWeight.setScale(0, RoundingMode.CEILING).longValue();
		return Long.valueOf(longValue);
	}

	public static Long getChargeableWeight(long volumetricWeight, long actualWeight) {
		long max = Math.max(volumetricWeight, actualWeight);
		return Long.valueOf(max);
	}

	public static Long getChargeableWeight(Long volumetricWeight, Long actualWeight, Long minimumWeight) {
		long max = NumberUtils.max(volumetricWeight.longValue(), actualWeight.longValue(), minimumWeight.longValue());
		return Long.valueOf(max);
	}

	public static BigDecimal getChargeableWeightForOldService(Long chargeableWeight, Long minimumWeight) {
		return BigDecimal.valueOf(Math.max(chargeableWeight.longValue(), minimumWeight.longValue()));
	}

	public static boolean isNullOrZero(Long value) {
		boolean result = false;
		if (ObjectUtils.isEmpty(value) || LONG_ZERO.equals(value))
			result = true;
		else
			result = false;

		return result;
	}

	public static BigDecimal percentage(BigDecimal base, BigDecimal pct) {

		return base.multiply(pct).divide(HUNDRED_BIG_DECIMAL);
	}
	
	public static String getCurrentDate() {
		
		LocalDate currDate = LocalDate.now();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(currDate.getYear());
		sb.append("-");
		sb.append(currDate.getMonthValue());
		sb.append("-");
		sb.append(currDate.getDayOfMonth());
		
		return sb.toString();
	}

}
