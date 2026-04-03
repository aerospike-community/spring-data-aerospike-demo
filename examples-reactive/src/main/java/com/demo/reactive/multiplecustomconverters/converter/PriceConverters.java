package com.demo.reactive.multiplecustomconverters.converter;

import com.demo.reactive.multiplecustomconverters.entity.Price;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

public class PriceConverters {

    @WritingConverter
    public enum PriceToMapConverter implements Converter<Price, Map<String, Object>> {
        INSTANCE;

        @Override
        public Map<String, Object> convert(Price source) {
            return Map.of(
                "amount", source.amount(),
                "currency", source.currency()
            );
        }
    }

    @ReadingConverter
    public enum MapToPriceConverter implements Converter<Map<String, Object>, Price> {
        INSTANCE;

        @Override
        public Price convert(Map<String, Object> source) {
            double amount = ((Number) source.get("amount")).doubleValue();
            String currency = (String) source.get("currency");
            return new Price(amount, currency);
        }
    }
}
