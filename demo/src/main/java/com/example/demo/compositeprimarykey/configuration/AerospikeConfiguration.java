package com.example.demo.compositeprimarykey.configuration;

import com.example.demo.compositeprimarykey.entity.CommentsKey;
import com.example.demo.compositeprimarykey.repository.AerospikeCommentsRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import java.util.List;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = AerospikeCommentsRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    @Override
    protected List<Object> customConverters() {
        return List.of(
                CommentsKey.CommentsKeyToStringConverter.INSTANCE,
                CommentsKey.StringToCommentsKeyConverter.INSTANCE
        );
    }
}

