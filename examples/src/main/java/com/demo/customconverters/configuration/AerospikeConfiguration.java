package com.demo.customconverters.configuration;

import com.demo.customconverters.converter.ArticleDocumentConverters;
import com.demo.customconverters.converter.UserDataConverters;
import com.demo.customconverters.entity.ArticleDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import java.util.List;

@Configuration
@EnableAerospikeRepositories(basePackages = "com.demo.customconverters.repository")
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    @Value("${spring.data.aerospike.namespace}")
    private String namespace;

    @Override
    protected List<Object> customConverters() {
        return List.of(
                UserDataConverters.MapToUserDataToConverter.INSTANCE,
                UserDataConverters.UserDataToMapConverter.INSTANCE,
                ArticleDocumentConverters.AerospikeReadDataToArticleDocumentToConverter.INSTANCE,
                new ArticleDocumentConverters.ArticleDocumentToAerospikeWriteDataConverter(namespace,
                        ArticleDocument.SET_NAME)
        );
    }
}

