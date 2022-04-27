package cdh;

public class ExchangeRateModel{
	// {"from_currency":"NOK","conversion_date":"26-08-2020","conversion_rate":0.30646644192460926,"to_currency":"TND"}
	public String conversionDate;
	public String fromCurrency;
	public String toCurrency;
	public String conversionRate;
	public String getConversionDate() {
		return conversionDate;
	}
	public void setConversionDate(String conversionDate) {
		this.conversionDate = conversionDate;
	}
	public String getFromCurrency() {
		return fromCurrency;
	}
	public void setFromCurrency(String fromCurrency) {
		this.fromCurrency = fromCurrency;
	}
	public String getToCurrency() {
		return toCurrency;
	}
	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}
	public String getConversionRate() {
		return conversionRate;
	}
	public void setConversionRate(String conversionRate) {
		this.conversionRate = conversionRate;
	}
	
	

}
