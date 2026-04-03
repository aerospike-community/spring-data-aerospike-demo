package com.demo.reactive.multiplecustomconverters.converter;

import com.demo.reactive.multiplecustomconverters.entity.Address;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

public class AddressConverters {

    @WritingConverter
    public enum AddressToMapConverter implements Converter<Address, Map<String, Object>> {
        INSTANCE;

        @Override
        public Map<String, Object> convert(Address source) {
            return Map.of(
                "street", source.street(),
                "city", source.city(),
                "country", source.country()
            );
        }
    }

    @ReadingConverter
    public enum MapToAddressConverter implements Converter<Map<String, Object>, Address> {
        INSTANCE;

        @Override
        public Address convert(Map<String, Object> source) {
            String street = (String) source.get("street");
            String city = (String) source.get("city");
            String country = (String) source.get("country");
            return new Address(street, city, country);
        }
    }
}
