package com.example.demo.persistence.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated // add this annotation if you want @ConfigurationProperties to be validated!
@ConfigurationProperties("aerospike")
public class AerospikeConfigurationProperties {

    @NotEmpty
    String hosts;

    @NotEmpty
    String namespace;
}
