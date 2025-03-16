package com.demo.batchread;

import com.demo.batchread.entity.MovieDocumentForBatchRead;
import com.demo.batchread.repository.MovieRepositoryForBatchRead;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.core.model.GroupedEntities;
import org.springframework.data.aerospike.core.model.GroupedKeys;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchReadTests extends BatchReadAerospikeDemoApplicationTest {

    MovieDocumentForBatchRead movie1, movie2, movie3;
    List<MovieDocumentForBatchRead> allMovieDocuments;

    @Autowired
    MovieRepositoryForBatchRead repository;
    @Autowired
    AerospikeTemplate template;

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
        repository.saveAll(allMovieDocuments);
    }

    /* ------------- ↓ Batch read by ids ↓ ------------- */

    @Test
    public void findAllById_usingRepository() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        Iterable<MovieDocumentForBatchRead> resultsIterable = repository.findAllById(ids);
        assertThat(resultsIterable).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findByIds_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        List<MovieDocumentForBatchRead> resultsList = template.findByIds(ids, MovieDocumentForBatchRead.class);
        assertThat(resultsList).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findByIds_withCustomSetName_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        List<MovieDocumentForBatchRead> resultsList =
                template.findByIds(ids, MovieDocumentForBatchRead.class, "demo-batchRead-set");
        assertThat(resultsList).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findByIds_withGroupedKeys_usingTemplate() {
        List<String> ids = allMovieDocuments.stream().map(MovieDocumentForBatchRead::getId).toList();
        GroupedKeys groupedKeys = GroupedKeys.builder().entityKeys(MovieDocumentForBatchRead.class, ids).build();
        GroupedEntities groupedEntities = template.findByIds(groupedKeys);
        assertThat(groupedEntities.containsEntities()).isTrue();
        assertThat(groupedEntities.getEntitiesByClass(MovieDocumentForBatchRead.class))
                .hasSameElementsAs(allMovieDocuments);
    }

    /* ------------- ↑ Batch read by ids ↑ ------------- */

    /* ------------- ↓ Batch read by entities ↓ ------------- */

    @Test
    public void findAll_usingRepository() {
        // This read operation is a scan (query without secondary index filter)
        Iterable<MovieDocumentForBatchRead> resultsIterable = repository.findAll();
        assertThat(resultsIterable).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findAll_withSorting_usingRepository() {
        // This read operation is a scan (query without secondary index filter)
        Iterable<MovieDocumentForBatchRead> resultsIterable = repository.findAll(Sort.by("rating"));
        List<MovieDocumentForBatchRead> actualResultsList = StreamSupport
                .stream(resultsIterable.spliterator(), false)
                .toList();
        assertThat(actualResultsList)
                .hasSameElementsAs(allMovieDocuments)
                .first().isEqualTo(movie3);

        Iterable<MovieDocumentForBatchRead> resultsIterableReversed =
                repository.findAll(Sort.by("rating").reverse());
        actualResultsList = StreamSupport
                .stream(resultsIterableReversed.spliterator(), false)
                .toList();
        assertThat(actualResultsList)
                .hasSameElementsAs(allMovieDocuments)
                .first().isEqualTo(movie1);
    }

    @Test
    public void findAll_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        Stream<MovieDocumentForBatchRead> resultsStream = template.findAll(MovieDocumentForBatchRead.class);
        assertThat(resultsStream.collect(Collectors.toList())).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findAll_withCustomSetName_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        Stream<MovieDocumentForBatchRead> resultsStream =
                template.findAll(MovieDocumentForBatchRead.class, "demo-batchRead-set");
        assertThat(resultsStream.collect(Collectors.toList())).hasSameElementsAs(allMovieDocuments);
    }

    @Test
    public void findAll_withSorting_usingTemplate() {
        // This read operation is a scan (query without secondary index filter)
        Stream<MovieDocumentForBatchRead> resultsStream =
                template.findAll(Sort.by("rating"), 0, 0, MovieDocumentForBatchRead.class);
        assertThat(resultsStream.collect(Collectors.toList()))
                .hasSameElementsAs(allMovieDocuments)
                .first().isEqualTo(movie3);
    }

    /* ------------- ↑ Batch read by entities ↑ ------------- */
}
