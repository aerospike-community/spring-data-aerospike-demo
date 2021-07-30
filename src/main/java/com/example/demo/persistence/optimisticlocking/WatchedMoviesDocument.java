package com.example.demo.persistence.optimisticlocking;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Document
public class WatchedMoviesDocument {

    @Id
    String key;

    @Singular
    List<String> watchedMovies;

    @NonFinal
    @Version  // <1>
    Long version;
}
