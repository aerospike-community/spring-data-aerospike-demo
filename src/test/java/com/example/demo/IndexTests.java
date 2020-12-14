package com.example.demo;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Info;
import com.aerospike.client.cluster.Node;
import com.example.demo.persistence.configuration.AerospikeConfiguration;
import com.example.demo.persistence.index.AerospikeIndexConfiguration;
import com.example.demo.service.OperationsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {AerospikeConfiguration.class,
        OperationsConfiguration.class, AerospikeIndexConfiguration.class})
public class IndexTests extends DemoApplicationTests {

    @Value("${embedded.aerospike.namespace}")
    String namespace;

    @Autowired
    AerospikeClient client;

    @Test
    void verifyCustomIndexCreated() {
        List<String> existingIndexes = getIndexes(client, namespace);

        assertThat(existingIndexes).contains("movie-rating-index");
    }

    @Test
    void verifyAnnotationBasedIndexesCreated() {
        List<String> existingIndexes = getIndexes(client, namespace);

        assertThat(existingIndexes)
                .contains(
                        "IndexedDocument_author_string_default",
                        "IndexedDocument_likes_numeric_default",
                        "IndexedDocument_options_numeric_list");
    }

    // DO NOT USE THIS CODE IN PRODUCTION
    private static List<String> getIndexes(AerospikeClient client, String namespace) {
        Node node = client.getNodes()[0];
        String response = Info.request(node, "sindex/" + namespace);
        return Arrays.stream(response.split(";"))
                .map(info -> {
                    Map<String, String> keyValue = Arrays.stream(info.split(":"))
                            .map(part -> {
                                String[] kvParts = part.split("=");
                                return Map.entry(kvParts[0], kvParts[1]);
                            })
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    return keyValue.get("indexname");
                })
                .collect(Collectors.toList());
    }

}
