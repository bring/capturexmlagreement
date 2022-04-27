package cdh;

import java.util.Map;

public interface ExchangeRateCacheService {
	
	public Map<String, ExchangeRateModel>  getCurrentDateExchangeCurrencies(String currency);
	
	public ExchangeRateModel  getCurrentDateExchangeRate(String fromCurrency, String toCurrency);
		
}
