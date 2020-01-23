package com.example.demo.persistence;

import com.aerospike.client.Host;
import com.example.demo.persistence.article.ArticleDocument;
import com.example.demo.persistence.article.ArticleDocumentConverters;
import com.example.demo.persistence.user.UserDataConverters;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

@EnableAerospikeRepositories(basePackageClasses = MovieRepository.class)
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
    @Validated
    @ConfigurationProperties("aerospike")
    public static class AerospikeConfigurationProperties {

        @NotEmpty
        String hosts;

        @NotEmpty
        String namespace;
    }
}