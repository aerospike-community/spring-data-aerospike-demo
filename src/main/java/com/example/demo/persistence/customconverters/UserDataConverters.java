package com.example.demo.persistence.customconverters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

public class UserDataConverters {

    @WritingConverter
    public enum UserDataToMapConverter implements Converter<UserDocument.UserData, Map<String, Object>> {
        INSTANCE;

        @Override
        public Map<String, Object> convert(UserDocument.UserData source) {
            Map<String, Object> map = Map.of(
                    "addr", source.getAddress().toUpperCase(),
                    "country", source.getCountry().toUpperCase()
            );
            return map;
        }
    }

    @ReadingConverter
    public enum MapToUserDataToConverter implements Converter<Map<String, Object>, UserDocument.UserData> {
        INSTANCE;

        @Override
        public UserDocument.UserData convert(Map<String, Object> source) {
            String address = (String) source.getOrDefault("addr", "N/A");
            String country = (String) source.getOrDefault("country", "N/A");
            return new UserDocument.UserData(address, country);
        }
    }
}
