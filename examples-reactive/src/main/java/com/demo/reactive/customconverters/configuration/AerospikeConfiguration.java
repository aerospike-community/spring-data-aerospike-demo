package com.demo.reactive.customconverters.configuration;

import com.demo.reactive.customconverters.converter.ArticleDocumentConverters;
import com.demo.reactive.customconverters.converter.UserDataConverters;
import com.demo.reactive.customconverters.entity.ArticleDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.List;

@Configuration
@EnableReactiveAerospikeRepositories(basePackages = "com.demo.reactive.customconverters.repository")
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

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
