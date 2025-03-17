package com.demo.reactive.batchread;

import com.demo.reactive.batchread.entity.MovieDocumentForBatchRead;
import com.demo.reactive.batchread.repository.ReactiveMovieRepositoryForBatchRead;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.core.model.GroupedKeys;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchReadTests extends ReactiveBatchReadAerospikeDemoApplicationTest {

    MovieDocumentForBatchRead movie1, movie2, movie3;
    List<MovieDocumentForBatchRead> allMovieDocuments;

    @Autowired
    ReactiveMovieRepositoryForBatchRead repository;
    @Autowired
    ReactiveAerospikeTemplate template;

    @BeforeAll
    void setUp() {
        movie1 = MovieDocumentForBatchRead.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future")
                .description("I finally invented something that works!")
                .rating(9.3)
                .likes(555_555)
                .build();
        movie2 = MovieDocumentForBatchRead.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future Part II")
                .description("Getting back was only the beginning.")
                .rating(7.8)
                .likes(555_555)
                .build();
        movie3 = MovieDocumentForBatchRead.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future III")
                .description("They've saved the best trip for last... But this time they may have gone too far.")
                .rating(7.5)
                .likes(555_555)
                .build();

        allMovieDocuments = List.of(movie1, movie2, movie3);
        repository.saveAll(allMovieDocuments).collectList().block();
    }

    /* ------------- ↓ Batch read by ids ↓ ------------- */

    @Test
    public void findAllById_usingRepository() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        StepVerifier.create(repository.findAllById(ids).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findByIds_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        StepVerifier.create(template.findByIds(ids, MovieDocumentForBatchRead.class).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findByIds_withCustomSetName_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        StepVerifier.create(template.findByIds(ids, MovieDocumentForBatchRead.class, "demo-batchRead-reactive-set")
                        .collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findByIds_withGroupedKeys_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        GroupedKeys groupedKeys = GroupedKeys.builder().entityKeys(MovieDocumentForBatchRead.class, ids).build();
        StepVerifier.create(template.findByIds(groupedKeys))
                .assertNext(results -> {
                    assertThat(results.containsEntities()).isTrue();
                    assertThat(results.getEntitiesByClass(MovieDocumentForBatchRead.class))
                            .hasSameElementsAs(allMovieDocuments);
                })
                .verifyComplete();
    }

    /* ------------- ↑ Batch read by ids ↑ ------------- */

    /* ------------- ↓ Batch read by entities ↓ ------------- */

    @Test
    public void findAll_usingRepository() {
        // This read operation is a scan (query without secondary index filter)
        StepVerifier.create(repository.findAll().collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findAll_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        StepVerifier.create(template.findAll(MovieDocumentForBatchRead.class).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findAll_withCustomSetName_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        StepVerifier.create(template.findAll(MovieDocumentForBatchRead.class, "demo-batchRead-reactive-set")
                        .collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void findAll_withSorting_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        StepVerifier.create(template.findAll(Sort.by("rating"), 0, 0, MovieDocumentForBatchRead.class)
                        .collectList())
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(allMovieDocuments);
                    assertThat(results).first().isEqualTo(movie3);
                })
                .verifyComplete();

        // This read operation is a scan (query without secondary index filter)
        StepVerifier.create(template.findAll(Sort.by("rating").reverse(), 0, 0, MovieDocumentForBatchRead.class)
                        .collectList())
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(allMovieDocuments);
                    assertThat(results).first().isEqualTo(movie1);
                })
                .verifyComplete();
    }

    /* ------------- ↑ Batch read by entities ↑ ------------- */
}
