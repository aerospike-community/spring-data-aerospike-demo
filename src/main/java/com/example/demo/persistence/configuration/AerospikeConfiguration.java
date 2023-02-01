package com.example.demo.persistence.configuration;

import com.aerospike.client.Host;
import com.example.demo.persistence.compositeprimarykey.CommentsKey;
import com.example.demo.persistence.customconverters.ArticleDocument;
import com.example.demo.persistence.customconverters.ArticleDocumentConverters;
import com.example.demo.persistence.customconverters.UserDataConverters;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

@EnableAerospikeRepositories(basePackages = "com.example.demo.persistence")
@EnableConfigurationProperties(AerospikeConfiguration.AerospikeConfigurationProperties.class)
@Configuration
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    @Autowired
    AerospikeConfigurationProperties properties;

    @Override
    protected Collection<Host> getHosts() {
        return Host.parseServiceHosts(properties.getHosts());
    }

    @Override
    protected String nameSpace() {
        return properties.getNamespace();
    }

    // Optional. Only needed when you need custom converters
    @Override
    protected List<?> customConverters() {
        return List.of(
                CommentsKey.CommentsKeyToStringConverter.INSTANCE,
                CommentsKey.StringToCommentsKeyConverter.INSTANCE,
                UserDataConverters.MapToUserDataToConverter.INSTANCE,
                UserDataConverters.UserDataToMapConverter.INSTANCE,
                ArticleDocumentConverters.AerospikeReadDataToArticleDocumentToConverter.INSTANCE,
                new ArticleDocumentConverters.ArticleDocumentToAerospikeWriteDataConverter(properties.getNamespace(), ArticleDocument.SET_NAME)
        );
    }

    @Data
    @Validated // add this annotation if you want @ConfigurationProperties to be validated!
    @ConfigurationProperties("aerospike")
    public static class AerospikeConfigurationProperties {

        @NotEmpty
        String hosts;

        @NotEmpty
        String namespace;
    }
}