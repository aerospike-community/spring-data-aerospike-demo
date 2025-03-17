package com.demo.batchwrite;

import com.aerospike.client.AerospikeException;
import com.demo.batchwrite.entity.MovieDocumentForBatchWrite;
import com.demo.batchwrite.entity.MovieDocumentForBatchWriteVersioned;
import com.demo.batchwrite.repository.MovieRepositoryForBatchWrite;
import com.demo.batchwrite.repository.MovieRepositoryForBatchWriteVersioned;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.core.model.GroupedKeys;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchWriteTests extends BatchWriteAerospikeDemoApplicationTest {

    MovieDocumentForBatchWrite movie1, movie2, movie3;
    List<MovieDocumentForBatchWrite> allMovieDocuments;
    List<String> allMovieDocumentsIds;

    @Autowired
    MovieRepositoryForBatchWrite repository;
    @Autowired
    MovieRepositoryForBatchWriteVersioned repositoryVersioned;
    @Autowired
    AerospikeTemplate template;

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
        assertThat(template.findByIds(allMovieDocumentsIds, MovieDocumentForBatchWrite.class)).isEmpty();
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
        repositoryVersioned.deleteAll();
    }

    /* ------------- ↓ Batch save ↓ ------------- */

    @Test
    public void saveAll_usingRepository() {
        repository.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void saveAll_usingRepository_idMustNotBeNull() {
        var moviesWithoutIds = List.of(
                MovieDocumentForBatchWrite.builder().build(),
                MovieDocumentForBatchWrite.builder().build()
        );
        assertThatThrownBy(() -> repository.saveAll(moviesWithoutIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id must not be null!");
    }

    @Test
    public void saveAll_theSameDocuments_usingRepository() {
        // Batch save of the same non-versioned documents multiple times results in overwriting them
        repository.saveAll(allMovieDocuments);
        repository.saveAll(allMovieDocuments);
        repository.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void saveAll_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void saveAll_withCustomSetName_usingTemplate() {
        template.saveAll(allMovieDocuments, "demo-batchWrite-set");
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void saveAll_versionedEntities_theSameDocuments_usingRepository() {
        // Batch save of the same versioned documents without explicitly set version multiple times
        // results in overwriting them with incrementing version automatically
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        repositoryVersioned.saveAll(allMovieDocumentsVersioned); // version becomes 1
        repositoryVersioned.saveAll(allMovieDocumentsVersioned); // version becomes 2
        repositoryVersioned.saveAll(allMovieDocumentsVersioned); // version becomes 3
        var results = repositoryVersioned.findAllById(List.of("id1", "id2"));
        assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned);
        assertThat(results.iterator().next().getVersion()).isEqualTo(3);

        // Version can be explicitly set manually too
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned4 = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(3).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(3).build()
        );
        repositoryVersioned.saveAll(allMovieDocumentsVersioned4); // version becomes 4
        assertThat(repositoryVersioned.findAllById(List.of("id1", "id2"))).hasSameElementsAs(List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(4).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(4).build()
        ));
    }

    @Test
    public void saveAll_versionedEntities_usingTemplate() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        // Saving documents with version 0
        template.saveAll(allMovieDocumentsVersioned);
        // Version becomes 1
        var results = repositoryVersioned.findAllById(List.of("id1", "id2"));
        assertThat(results).hasSameElementsAs(allMovieDocumentsVersioned);
        assertThat(results.iterator().next().getVersion()).isEqualTo(1);

        // Records with the same ids are already saved with version 1
        var allMovieDocumentsVersioned0 = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        );
        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> template.saveAll(allMovieDocumentsVersioned0))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to save the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch save ↑ ------------- */

    /* ------------- ↓ Batch insert ↓ ------------- */

    @Test
    public void insertAll_usingTemplate() {
        template.insertAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void insertAll_withCustomSetName_usingTemplate() {
        template.insertAll(allMovieDocuments, "demo-batchWrite-set");
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void insertAll_usingTemplate_idsMustNotBeIdentical() {
        var moviesWithIdenticalIds = List.of(
                MovieDocumentForBatchWrite.builder().id("id").build(),
                MovieDocumentForBatchWrite.builder().id("id").build()
        );
        assertThatThrownBy(() -> template.insertAll(moviesWithIdenticalIds))
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
        repository.saveAll(movies);
        assertThat(repository.findAllById(List.of("id1", "id2"))).hasSameElementsAs(movies);

        var moviesUpdated = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(200).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(200).build()
        );;
        template.updateAll(moviesUpdated);
        assertThat(repository.findAllById(List.of("id1", "id2"))).hasSameElementsAs(moviesUpdated);
    }

    @Test
    public void updateAll_usingTemplate_idsMusNotBeIdentical() {
        var moviesWithIdenticalIds = List.of(
                MovieDocumentForBatchWrite.builder().id("id").build(),
                MovieDocumentForBatchWrite.builder().name("name").id("id").build()
        );
        assertThatThrownBy(() -> template.updateAll(moviesWithIdenticalIds))
                .isInstanceOf(AerospikeException.BatchRecordArray.class)
                .hasMessageContaining("Batch failed");
    }

    @Test
    public void updateAll_withCustomSetName_usingTemplate() {
        var movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(100).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(100).build()
        );
        repository.saveAll(movies);
        assertThat(repository.findAllById(List.of("id1", "id2"))).hasSameElementsAs(movies);

        var moviesUpdated = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").likes(200).build(),
                MovieDocumentForBatchWrite.builder().id("id2").likes(200).build()
        );;
        template.updateAll(moviesUpdated, "demo-batchWrite-set");
        assertThat(repository.findAllById(List.of("id1", "id2"))).hasSameElementsAs(moviesUpdated);
    }

    @Test
    public void updateAll_versionedEntities_usingTemplate() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        repositoryVersioned.saveAll(allMovieDocumentsVersioned);
        assertThat(repositoryVersioned.findAllById(List.of("id1", "id2"))).hasSameElementsAs(allMovieDocumentsVersioned);

        var versionedMovieDocumentsUpdated = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(1).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(1).build()
        );
        template.updateAll(versionedMovieDocumentsUpdated);
        assertThat(repositoryVersioned.findAllById(List.of("id1", "id2"))).hasSameElementsAs(versionedMovieDocumentsUpdated);
    }

    @Test
    public void updateAll_versionedEntities_usingTemplate_versionsMismatch() {
        List<MovieDocumentForBatchWriteVersioned> allMovieDocumentsVersioned = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        repositoryVersioned.saveAll(allMovieDocumentsVersioned);
        assertThat(repositoryVersioned.findAllById(List.of("id1", "id2"))).hasSameElementsAs(allMovieDocumentsVersioned);

        var versionedMovieDocumentsUpdated = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        );
        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> template.updateAll(versionedMovieDocumentsUpdated))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to update the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch update ↑ ------------- */

    /* ------------- ↓ Batch delete ↓ ------------- */

    @Test
    public void deleteAllById_usingRepository() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        repository.deleteAllById(allMovieDocumentsIds);
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAllById_usingRepository_idsMustNotBeIdentical() {
        List<MovieDocumentForBatchWrite> movies = List.of(
                MovieDocumentForBatchWrite.builder().id("id1").build(),
                MovieDocumentForBatchWrite.builder().id("id2").build()
        );
        template.saveAll(movies);
        assertThat(repository.findAllById(List.of("id1", "id2"))).hasSameElementsAs(movies);

        assertThatThrownBy(() -> repository.deleteAllById(List.of("id1", "id1", "id2", "id2")))
                .isInstanceOf(AerospikeException.BatchRecordArray.class)
                .hasMessageContaining("Batch failed");
    }

    @Test
    public void deleteByIds_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteByIds(allMovieDocumentsIds, MovieDocumentForBatchWrite.class);
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();

        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteByIds(allMovieDocumentsIds, "demo-batchWrite-set");
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteByIds_withGroupedKeys_usingTemplate() {
        template.saveAll(allMovieDocuments);
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchWrite::getId).toList();
        GroupedKeys groupedKeys = GroupedKeys.builder().entityKeys(MovieDocumentForBatchWrite.class, ids).build();
        template.deleteByIds(groupedKeys);
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAll_usingRepository() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        repository.deleteAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAll_truncate_usingRepository() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        repository.deleteAll(); // deletes all entities managed by the repository
        await()
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty());
    }

    @Test
    public void deleteAll_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAll_withCustomSetName_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteAll(allMovieDocuments, "demo-batchWrite-set");
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAll_truncate_byClass_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteAll(MovieDocumentForBatchWrite.class); // deletes all entities of the class
        await()
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty());
    }

    @Test
    public void deleteAll_truncate_bySet_usingTemplate() {
        template.saveAll(allMovieDocuments);
        assertThat(repository.findAllById(allMovieDocumentsIds)).hasSameElementsAs(allMovieDocuments);

        template.deleteAll("demo-batchWrite-set"); // deletes all entities in the set
        await()
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty());
    }

    @Test
    public void deleteAllById_versionedEntities_usingRepository() {
        List<MovieDocumentForBatchWriteVersioned> movies = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        // Version is implicitly set to 0
        repositoryVersioned.saveAll(movies);
        // Version becomes 1
        var results = repositoryVersioned.findAllById(List.of("id1", "id2"));
        assertThat(results).hasSameElementsAs(movies);
        assertThat(results.iterator().next().getVersion()).isEqualTo(1);

        repositoryVersioned.deleteAllById(List.of("id1", "id2"));
        assertThat(repository.findAllById(allMovieDocumentsIds)).isEmpty();
    }

    @Test
    public void deleteAll_versionedEntities_usingRepository_versionsMismatch() {
        List<MovieDocumentForBatchWriteVersioned> movies = List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").build()
        );
        // Version is implicitly set to 0
        repositoryVersioned.saveAll(movies);
        // Version becomes 1
        var results = repositoryVersioned.findAllById(List.of("id1", "id2"));
        assertThat(results).hasSameElementsAs(movies);
        assertThat(results.iterator().next().getVersion()).isEqualTo(1);

        // Will produce optimistic locking exception because of versions mismatch
        assertThatThrownBy(() -> repositoryVersioned.deleteAll(List.of(
                MovieDocumentForBatchWriteVersioned.builder().id("id1").version(0).build(),
                MovieDocumentForBatchWriteVersioned.builder().id("id2").version(0).build()
        )))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Failed to delete the record with ID")
                .hasMessageContaining("due to versions mismatch");
    }

    /* ------------- ↑ Batch delete ↑ ------------- */
}
