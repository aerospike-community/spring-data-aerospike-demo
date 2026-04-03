package com.demo.reactive.multiplecustomconverters.configuration;

import com.demo.reactive.multiplecustomconverters.converter.AddressConverters;
import com.demo.reactive.multiplecustomconverters.converter.PriceConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.convert.AerospikeCustomConverters;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.List;

/**
 * Demonstrates registering multiple {@link AerospikeCustomConverters} beans in a reactive application.
 * <p>
 * Since SDA 6.0.0, {@code AerospikeDataConfigurationSupport.customConversions()} accepts an
 * {@code ObjectProvider<AerospikeCustomConverters>} and aggregates all beans via
 * {@code orderedStream()}. Each bean contributes its own group of converters independently.
 */
@Configuration
@EnableReactiveAerospikeRepositories(basePackages = "com.demo.reactive.multiplecustomconverters.repository")
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

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
