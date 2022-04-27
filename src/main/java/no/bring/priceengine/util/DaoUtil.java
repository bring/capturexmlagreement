package no.bring.priceengine.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DaoUtil {

	
	public static void setDateTime(LocalDateTime localDateTime, PreparedStatement preparedStatement, Integer setterIndex)
			throws SQLException {

		if (localDateTime == null)
			preparedStatement.setNull(setterIndex++, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
		else
			preparedStatement.setTimestamp(setterIndex++, Timestamp.valueOf(localDateTime));
		
	}
	
	public static void setBoolean(Boolean isSuccessful,PreparedStatement preparedStatement,int setterIndex) 
			throws SQLException {
		
			preparedStatement.setBoolean(setterIndex++, isSuccessful);
			
		}
	

	
	public static void setDate(LocalDate localDate, PreparedStatement preparedStatement, int setterIndex)
			throws SQLException {

		if (localDate == null)
			preparedStatement.setNull(setterIndex++, java.sql.Types.DATE);
		else
			preparedStatement.setDate(setterIndex++, Date.valueOf(localDate));
	}

	public static void setInteger(Integer intValue, PreparedStatement preparedStatement, int setterIndex)
			throws SQLException {
		if (intValue == null)
			preparedStatement.setNull(setterIndex, java.sql.Types.INTEGER);
		else
			preparedStatement.setLong(setterIndex, intValue.intValue());

	}

	public static void setLong(Long longValue, PreparedStatement preparedStatement, int setterIndex)
			throws SQLException {
		if (longValue == null)
			preparedStatement.setNull(setterIndex, java.sql.Types.BIGINT);
		else
			preparedStatement.setLong(setterIndex, longValue.longValue());

	}

	public static void setString(String stringValue, PreparedStatement preparedStatement, int setterIndex)
			throws SQLException {
		if (stringValue == null)
			preparedStatement.setNull(setterIndex, java.sql.Types.VARCHAR);
		else
			preparedStatement.setString(setterIndex, stringValue);

	}
	

	public static void setBigDecimal(BigDecimal bigDecimalValue, PreparedStatement preparedStatement, int setterIndex)
			throws SQLException {
		if (bigDecimalValue == null)
			preparedStatement.setNull(setterIndex, java.sql.Types.DECIMAL);
		else
			preparedStatement.setBigDecimal(setterIndex, bigDecimalValue);

	}

	


	
	
	

}
