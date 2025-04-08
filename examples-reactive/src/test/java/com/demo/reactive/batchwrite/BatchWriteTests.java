package com.demo.reactive.batchwrite;

import com.aerospike.client.AerospikeException;
import com.demo.reactive.batchwrite.entity.MovieDocumentForBatchWrite;
import com.demo.reactive.batchwrite.entity.MovieDocumentForBatchWriteVersioned;
import com.demo.reactive.batchwrite.repository.ReactiveMovieRepositoryForBatchWrite;
import com.demo.reactive.batchwrite.repository.ReactiveMovieRepositoryForBatchWriteVersioned;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.core.model.GroupedKeys;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchWriteTests extends ReactiveBatchWriteAerospikeDemoApplicationTest {

    MovieDocumentForBatchWrite movie1, movie2, movie3;
    List<MovieDocumentForBatchWrite> allMovieDocuments;
    List<String> allMovieDocumentsIds;

    @Autowired
    ReactiveMovieRepositoryForBatchWrite repository;
    @Autowired
    ReactiveMovieRepositoryForBatchWriteVersioned repositoryVersioned;
    @Autowired
    ReactiveAerospikeTemplate template;

    @BeforeEach
    void setUp() {
        movie1 = MovieDocumentForBatchWrite.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future")
                .description("I finally invented something that works!")
                .rating(9.3)
                .likes(555_555)
                .build();
        movie2 = MovieDocumentForBatchWrite.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future Part II")
                .description("Getting back was only the beginning.")
                .rating(7.8)
                .likes(555_555)
                .build();
        movie3 = MovieDocumentForBatchWrite.builder()
                .id(UUID.randomUUID().toString())
                .name("Back To the Future III")
                .description("They've saved the best trip for last... But this time they may have gone too far.")
                .rating(7.5)
                .likes(555_555)
                .build();

        allMovieDocuments = List.of(movie1, movie2, movie3);
        allMovieDocumentsIds = allMovieDocuments.stream().map(MovieDocumentForBatchWrite::getId).toList();
        StepVerifier.create(template.findByIds(allMovieDocumentsIds, MovieDocumentForBatchWrite.class).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll().block();
        repositoryVersioned.deleteAll().block();
    }

    /* ------------- ↓ Batch save ↓ ------------- */

    @Test
    public void saveAll_usingRepository() {
        StepVerifier.create(repository.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void saveAll_usingRepository_idMustNotBeNull() {
        var moviesWithoutIds = List.of(
                MovieDocumentForBatchWrite.builder().build(),
                MovieDocumentForBatchWrite.builder().build()
        );
        assertThatThrownBy(() -> repository.saveAll(moviesWithoutIds).collectList().block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id must not be null!");
    }

    @Test
    public void saveAll_theSameDocuments_usingRepository() {
        // Batch save of the same non-versioned documents multiple times results in overwriting them
        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.saveAll(allMovieDocuments))
                        .thenMany(repository.saveAll(allMovieDocuments))
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void saveAll_usingTemplate() {
        StepVerifier.create(template.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void saveAll_withCustomSetName_usingTemplate() {
        StepVerifier.create(template.saveAll(allMovieDocuments, "demo-batchWrite-reactive-set")
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void saveAll_versionedEntities_theSameDocuments_usingRepository() {
        // Batch save of the same versioned documents without explicitly set version multiple times
        // results in overwriting them with incrementing version automatically
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );

        StepVerifier.create(
                        Mono.defer(() -> repositoryVersioned.saveAll(allMovieDocumentsVersioned).then())
                                // version is now 1
                                .then(Mono.defer(() -> repositoryVersioned.saveAll(allMovieDocumentsVersioned).then()))
                                // version is now 2
                                .then(Mono.defer(() -> repositoryVersioned.saveAll(allMovieDocumentsVersioned).then()))
                                // version is now 3
                                .then(repositoryVersioned.findAllById(List.of("id1", "id2")).collectList())
                )
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned);
                    assertThat(results.get(0).getVersion()).isEqualTo(3);
                })
                .verifyComplete();


        // Version can be explicitly set manually too
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned2 = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(3).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(3).build()
        );

        Mono<List<MovieDocumentForBatchWriteVersioned>> secondResultsMono = repositoryVersioned.saveAll(allMovieDocumentsVersioned2) // version becomes 4
                .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2")))
                .collectList();

        // Verify results
        StepVerifier.create(secondResultsMono)
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(List.of(
                            MovieDocumentForBatchWriteVersioned.builder().id("id1").version(4).build(),
                            MovieDocumentForBatchWriteVersioned.builder().id("id2").version(4).build()
                    ));
                })
                .verifyComplete();
    }

    @Test
    public void saveAll_versionedEntities_usingTemplate() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        StepVerifier.create(template.saveAll(allMovieDocumentsVersioned)
                        .then(repositoryVersioned.findAllById(List.of("id1", "id2")).collectList()))
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned);
                    // Version becomes 1
                    assertThat(results.iterator().next().getVersion()).isEqualTo(1);
                })
                .verifyComplete();

        // Records with the same ids are already saved with version 1
        var allMovieDocumentsVersioned0 = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        );
        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> template.saveAll(allMovieDocumentsVersioned0).collectList().block())
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to save the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch save ↑ ------------- */

    /* ------------- ↓ Batch insert ↓ ------------- */

    @Test
    public void insertAll_usingTemplate() {
        StepVerifier.create(template.insertAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void insertAll_withCustomSetName_usingTemplate() {
        StepVerifier.create(template.insertAll(allMovieDocuments, "demo-batchWrite-reactive-set")
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();
    }

    @Test
    public void insertAll_usingTemplate_idsMustNotBeIdentical() {
        var moviesWithIdenticalIds = List.of(
                MovieDocumentForBatchWrite.builder().id("id").build(),
                MovieDocumentForBatchWrite.builder().id("id").build()
        );
        assertThatThrownBy(() -> template.insertAll(moviesWithIdenticalIds).collectList().block())
                .isInstanceOf(AerospikeException.BatchRecordArray.class)
                .hasMessageContaining("Batch failed");
    }

    /* ------------- ↑ Batch insert ↑ ------------- */

    /* ------------- ↓ Batch update ↓ ------------- */

    @Test
    public void updateAll_usingTemplate() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        StepVerifier.create(repository.saveAll(movies)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(movies))
                .verifyComplete();

        var moviesUpdated = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(200).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(200).build()
        );
        StepVerifier.create(template.updateAll(moviesUpdated)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(moviesUpdated))
                .verifyComplete();
    }

    @Test
    public void updateAll_usingTemplate_idsMusNotBeIdentical() {
        var moviesWithIdenticalIds = List.of(
                MovieDocumentForBatchWrite.builder().id("id").build(),
                MovieDocumentForBatchWrite.builder().name("name").id("id").build()
        );
        assertThatThrownBy(() -> template.updateAll(moviesWithIdenticalIds).collectList().block())
                .isInstanceOf(AerospikeException.BatchRecordArray.class)
                .hasMessageContaining("Batch failed");
    }

    @Test
    public void updateAll_withCustomSetName_usingTemplate() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        StepVerifier.create(repository.saveAll(movies)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(movies))
                .verifyComplete();

        var moviesUpdated = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(200).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(200).build()
        );
        StepVerifier.create(template.updateAll(moviesUpdated, "demo-batchWrite-reactive-set")
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(moviesUpdated))
                .verifyComplete();
    }

    @Test
    public void updateAll_versionedEntities_usingTemplate() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        StepVerifier.create(repositoryVersioned.saveAll(allMovieDocumentsVersioned)
                        .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned))
                .verifyComplete();

        var versionedMovieDocumentsUpdated = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(1).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(1).build()
        );
        StepVerifier.create(template.updateAll(versionedMovieDocumentsUpdated)
                        .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(versionedMovieDocumentsUpdated))
                .verifyComplete();
    }

    @Test
    public void updateAll_versionedEntities_usingTemplate_versionsMismatch() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        StepVerifier.create(repositoryVersioned.saveAll(allMovieDocumentsVersioned)
                        .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned))
                .verifyComplete();

        // Saved records already have version 1
        var versionedMovieDocumentsUpdated = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        );
        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> template.updateAll(versionedMovieDocumentsUpdated).collectList().block())
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to update the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch update ↑ ------------- */

    /* ------------- ↓ Batch delete ↓ ------------- */

    @Test
    public void deleteAllById_usingRepository() {
        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        StepVerifier.create(repository.deleteAllById(allMovieDocumentsIds)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAllById_usingRepository_ignoresEmptyRecords() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        StepVerifier.create(repository.saveAll(movies)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(movies))
                .verifyComplete();

        StepVerifier.create(repository.deleteAllById(List.of("id1", "id1", "id2", "id2"))
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteByIds_usingTemplate() {
        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        StepVerifier.create(template.deleteByIds(allMovieDocumentsIds, MovieDocumentForBatchWrite.class)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();

        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        StepVerifier.create(template.deleteByIds(allMovieDocumentsIds, "demo-batchWrite-reactive-set")
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteByIds_usingTemplate_idsMustNotBeIdentical() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        StepVerifier.create(repository.saveAll(movies)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(movies))
                .verifyComplete();

        assertThatThrownBy(() ->
                template.deleteByIds(List.of("id1", "id1", "id2", "id2"), MovieDocumentForBatchWrite.class).block())
                .isInstanceOf(AerospikeException.BatchRecordArray.class)
                .hasMessageContaining("Batch failed");
    }

    @Test
    public void deleteExistingByIds_usingTemplate_ignoresEmptyRecords() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        StepVerifier.create(repository.saveAll(movies)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(movies))
                .verifyComplete();

        StepVerifier.create(template.deleteExistingByIds(List.of("id1", "id1", "id2", "id2"), MovieDocumentForBatchWrite.class)
                        .thenMany(repository.findAllById(List.of("id1", "id2"))).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteByIds_withGroupedKeys_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchWrite::getId).toList();
        GroupedKeys groupedKeys = GroupedKeys.builder().entityKeys(MovieDocumentForBatchWrite.class, ids).build();

        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        StepVerifier.create(template.deleteByIds(groupedKeys)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAll_usingRepository() {
        StepVerifier.create(repository.saveAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        StepVerifier.create(template.deleteAll(allMovieDocuments)
                        .thenMany(repository.findAllById(allMovieDocumentsIds)).collectList())
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAll_truncate_usingRepository() {
        template.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        repository.deleteAll()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        Mono.defer(() -> repository.findAllById(allMovieDocumentsIds).collectList())
                .map(list -> {
                    assertThat(list).isEmpty();
                    return list;
                })
                .retryWhen(Retry.fixedDelay(8, Duration.ofMillis(250))
                        .filter(throwable -> throwable instanceof AssertionError))
                .timeout(Duration.ofSeconds(2))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void deleteAll_usingTemplate() {
        template.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        template.deleteAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds).collectList())
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAll_withCustomSetName_usingTemplate() {
        template.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        template.deleteAll(allMovieDocuments, "demo-batchWrite-reactive-set")
                .thenMany(repository.findAllById(allMovieDocumentsIds).collectList())
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAll_truncate_byClass_usingTemplate() {
        template.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        template.deleteAll(MovieDocumentForBatchWrite.class)
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        Mono.defer(() -> repository.findAllById(allMovieDocumentsIds).collectList())
                .map(list -> {
                    assertThat(list).isEmpty();
                    return list;
                })
                .retryWhen(Retry.fixedDelay(8, Duration.ofMillis(250))
                        .filter(throwable -> throwable instanceof AssertionError))
                .timeout(Duration.ofSeconds(2))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void deleteAll_truncate_bySet_usingTemplate() {
        template.saveAll(allMovieDocuments)
                .thenMany(repository.findAllById(allMovieDocumentsIds))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).hasSameElementsAs(allMovieDocuments))
                .verifyComplete();

        template.deleteAll("demo-batchWrite-reactive-set")
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        Mono.defer(() -> repository.findAllById(allMovieDocumentsIds).collectList())
                .map(list -> {
                    assertThat(list).isEmpty();
                    return list;
                })
                .retryWhen(Retry.fixedDelay(8, Duration.ofMillis(250))
                        .filter(throwable -> throwable instanceof AssertionError))
                .timeout(Duration.ofSeconds(2))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void deleteAllById_versionedEntities_usingRepository() {
        // At first version is implicitly set to 0
        var movies = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").likes(100).build()
        );

        repositoryVersioned.saveAll(movies)
                .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2")))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(movies);
                    assertThat(results.iterator().next().getVersion()).isEqualTo(1);
                })
                .verifyComplete();

        repositoryVersioned.deleteAllById(List.of("id1", "id2"))
                .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2")).collectList())
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();
    }

    @Test
    public void deleteAll_versionedEntities_usingRepository_versionsMismatch() {
        var movies = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").likes(100).build()
        );

        repositoryVersioned.saveAll(movies)
                .thenMany(repositoryVersioned.findAllById(List.of("id1", "id2")))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> {
                    assertThat(results).hasSameElementsAs(movies);
                    assertThat(results.iterator().next().getVersion()).isEqualTo(1);
                })
                .verifyComplete();

        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> repositoryVersioned.deleteAll(List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        )).block())
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to delete the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch delete ↑ ------------- */
}
