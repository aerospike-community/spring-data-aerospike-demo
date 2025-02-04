package com.demo.index;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.cluster.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.aerospike.query.cache.IndexInfoParser;
import org.springframework.data.aerospike.query.model.Index;
import org.springframework.data.aerospike.util.InfoCommandUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexTests extends SecondaryIndexAerospikeDemoApplicationTest {

    @Value("${spring.data.aerospike.namespace}")
    private String namespace;

    @Autowired
    AerospikeClient client;

    IndexInfoParser indexInfoParser = new IndexInfoParser();

    @Test
    void verifyCustomIndexCreated() {
        List<String> existingIndexes = getIndexes(client, namespace, indexInfoParser)
                .stream()
                .map(Index::getName)
                .toList();
        assertThat(existingIndexes).contains("movie-rating-index");
    }

    @Test
    void verifyAnnotationBasedIndexesCreated() {
        List<String> existingIndexes = getIndexes(client, namespace, indexInfoParser)
                .stream()
                .map(Index::getName)
                .toList();
        assertThat(existingIndexes)
                .contains(
                        "IndexedDocument_author_string_default",
                        "IndexedDocument_likes_numeric_default",
                        "IndexedDocument_options_numeric_list");
    }

    // DO NOT USE THIS CODE IN PRODUCTION
    // Only for testing, subject to changes
    private static List<Index> getIndexes(IAerospikeClient client, String namespace, IndexInfoParser indexInfoParser) {
        Node node = client.getCluster().getRandomNode();
        String response = InfoCommandUtils.request(client, node, "sindex-list:ns=" + namespace + ";b64=true");
        return Arrays.stream(response.split(";"))
                .map(indexInfoParser::parse)
                .collect(Collectors.toList());
    }
}
