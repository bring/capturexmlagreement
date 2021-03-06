package no.bring.priceengine.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDate getDateStringAsLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDate date = LocalDate.parse(dateString, formatter);
		return date;
	}

	public static LocalDate getDateStringDDDOTMMDOTYYYYAsLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate date = LocalDate.parse(dateString, formatter);
		return date;
	}

	public static LocalDate getLMDateStringYYYYMMDDAsLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate date = LocalDate.parse(dateString, formatter);
		return date;
	}

	public static boolean isDateBetween(LocalDate checkDate, LocalDate startDate, LocalDate endDate) {
		boolean result = false;
		if (checkDate.compareTo(startDate) >= 0 && (endDate == null || checkDate.compareTo(endDate) <= 0))
			result = true;
		else
			result = false;

		return result;

	}

}
