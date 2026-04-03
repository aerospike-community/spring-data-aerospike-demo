package com.demo.multiplecustomconverters.configuration;

import com.demo.multiplecustomconverters.converter.AddressConverters;
import com.demo.multiplecustomconverters.converter.PriceConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.convert.AerospikeCustomConverters;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import java.util.List;

/**
 * Demonstrates registering multiple {@link AerospikeCustomConverters} beans.
 * <p>
 * Since SDA 6.0.0, {@code AerospikeDataConfigurationSupport.customConversions()} accepts an
 * {@code ObjectProvider<AerospikeCustomConverters>} and aggregates all beans via
 * {@code orderedStream()}. Each bean contributes its own group of converters independently.
 */
@Configuration
@EnableAerospikeRepositories(basePackages = "com.demo.multiplecustomconverters.repository")
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    @Bean
    public AerospikeCustomConverters priceConverters() {
        return new AerospikeCustomConverters(List.of(
            PriceConverters.PriceToMapConverter.INSTANCE,
            PriceConverters.MapToPriceConverter.INSTANCE
        ));
    }

    @Bean
    public AerospikeCustomConverters addressConverters() {
        return new AerospikeCustomConverters(List.of(
            AddressConverters.AddressToMapConverter.INSTANCE,
            AddressConverters.MapToAddressConverter.INSTANCE
        ));
    }
}
