package com.realshyt.fashion.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {
    
    // Conversion rates to IDR (Indonesian Rupiah) as base currency
    // These are approximate rates - in production, use a real-time currency API
    private static final Map<String, BigDecimal> CONVERSION_RATES = new HashMap<>();
    
    static {
        // Base: IDR (Indonesian Rupiah)
        CONVERSION_RATES.put("IDR", BigDecimal.ONE); // 1 IDR = 1 IDR
        
        // Other currencies to IDR
        CONVERSION_RATES.put("USD", new BigDecimal("16000"));  // 1 USD ≈ 16,000 IDR
        CONVERSION_RATES.put("EUR", new BigDecimal("17300"));  // 1 EUR ≈ 17,300 IDR
        CONVERSION_RATES.put("GBP", new BigDecimal("20200"));  // 1 GBP ≈ 20,200 IDR
        CONVERSION_RATES.put("SGD", new BigDecimal("11800"));  // 1 SGD ≈ 11,800 IDR
        CONVERSION_RATES.put("MYR", new BigDecimal("3400"));   // 1 MYR ≈ 3,400 IDR
        CONVERSION_RATES.put("AUD", new BigDecimal("10500"));  // 1 AUD ≈ 10,500 IDR
        CONVERSION_RATES.put("JPY", new BigDecimal("105"));     // 1 JPY ≈ 105 IDR
        CONVERSION_RATES.put("CNY", new BigDecimal("2200"));   // 1 CNY ≈ 2,200 IDR
        CONVERSION_RATES.put("HKD", new BigDecimal("2050"));   // 1 HKD ≈ 2,050 IDR
        CONVERSION_RATES.put("KRW", new BigDecimal("12"));      // 1 KRW ≈ 12 IDR
        CONVERSION_RATES.put("THB", new BigDecimal("450"));    // 1 THB ≈ 450 IDR
    }
    
    /**
     * Convert amount from source currency to target currency
     * @param amount - amount to convert
     * @param fromCurrency - source currency code
     * @param toCurrency - target currency code
     * @return converted amount
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) {
            return amount; // Return original if currencies are null
        }
        
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        // First convert from source to IDR
        BigDecimal fromRate = CONVERSION_RATES.getOrDefault(fromCurrency.toUpperCase(), BigDecimal.ONE);
        BigDecimal toIDR = amount.multiply(fromRate);
        
        // Then convert from IDR to target
        BigDecimal toRate = CONVERSION_RATES.getOrDefault(toCurrency.toUpperCase(), BigDecimal.ONE);
        
        if (toRate.equals(BigDecimal.ZERO)) {
            return amount; // Prevent division by zero
        }
        
        return toIDR.divide(toRate, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Convert amount from any currency to IDR
     */
    public BigDecimal toIDR(BigDecimal amount, String fromCurrency) {
        return convert(amount, fromCurrency, "IDR");
    }
    
    /**
     * Convert amount from IDR to any currency
     */
    public BigDecimal fromIDR(BigDecimal amount, String toCurrency) {
        return convert(amount, "IDR", toCurrency);
    }
    
    /**
     * Get supported currencies
     */
    public String[] getSupportedCurrencies() {
        return CONVERSION_RATES.keySet().toArray(new String[0]);
    }
    
    /**
     * Check if currency is supported
     */
    public boolean isCurrencySupported(String currency) {
        return CONVERSION_RATES.containsKey(currency.toUpperCase());
    }
    
    /**
     * Detect user region based on currency preference
     * In production, this would use geolocation or user preferences
     */
    public String detectUserCurrency(String userRegion) {
        Map<String, String> regionToCurrency = new HashMap<>();
        regionToCurrency.put("US", "USD");
        regionToCurrency.put("ID", "IDR");
        regionToCurrency.put("GB", "GBP");
        regionToCurrency.put("AU", "AUD");
        regionToCurrency.put("SG", "SGD");
        regionToCurrency.put("MY", "MYR");
        regionToCurrency.put("JP", "JPY");
        regionToCurrency.put("CN", "CNY");
        regionToCurrency.put("HK", "HKD");
        regionToCurrency.put("KR", "KRW");
        regionToCurrency.put("TH", "THB");
        regionToCurrency.put("EU", "EUR");
        
        return regionToCurrency.getOrDefault(userRegion, "IDR");
    }
}

