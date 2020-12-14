package com.example.demo.persistence.configuration;

import com.aerospike.client.Host;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.example.demo.persistence.compositeprimarykey.CommentsKey;
import com.example.demo.persistence.customconverters.ArticleDocument;
import com.example.demo.persistence.customconverters.ArticleDocumentConverters;
import com.example.demo.persistence.customconverters.UserDataConverters;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.Collection;
import java.util.List;

@EnableReactiveAerospikeRepositories(basePackages = "com.example.demo.persistence")
@EnableConfigurationProperties(AerospikeConfigurationProperties.class)
@Configuration
public class AerospikeReactiveConfiguration extends AbstractReactiveAerospikeDataConfiguration {

    private static final EventLoops eventLoops = new NettyEventLoops(new EventPolicy(), new NioEventLoopGroup(2));

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

    @Override
    protected EventLoops eventLoops() {
        return eventLoops;
    }

}