package com.demo.reactive.compositeprimarykey.configuration;

import com.demo.reactive.compositeprimarykey.entity.CommentsKey;
import com.demo.reactive.compositeprimarykey.repository.ReactiveCommentsRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.List;

@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveCommentsRepository.class)
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

    @Override
    protected List<Object> customConverters() {
        return List.of(
                CommentsKey.CommentsKeyToStringConverter.INSTANCE,
                CommentsKey.StringToCommentsKeyConverter.INSTANCE
        );
    }
}
