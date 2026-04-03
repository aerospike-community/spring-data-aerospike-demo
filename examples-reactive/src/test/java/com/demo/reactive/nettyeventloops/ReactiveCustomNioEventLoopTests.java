package com.demo.reactive.nettyeventloops;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.reactive.nettyeventloops.entity.MovieDocument;
import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "demo.eventloop.type=custom-nio")
public class ReactiveCustomNioEventLoopTests extends ReactiveNettyEventLoopsAerospikeDemoApplicationTest {

    @Autowired
    ReactiveMovieRepository repository;

    @Autowired
    EventLoops eventLoops;

    String id;
    MovieDocument movie;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID().toString();
        movie = MovieDocument.builder()
            .id(id)
            .name("The Matrix")
            .description("Reality is not what it seems")
            .rating(8.7)
            .likes(200_000)
            .build();
    }

    @Test
    void crudOperationsWork() {
        StepVerifier.create(
            repository.save(movie)
                .then(repository.findById(id))
        ).expectNext(movie).verifyComplete();

        StepVerifier.create(
            repository.deleteById(id)
                .then(repository.findById(id))
        ).verifyComplete();
    }

    @Test
    void eventLoopsUseNioWithCustomThreadCount() {
        assertThat(eventLoops).isInstanceOf(NettyEventLoops.class);

        NettyEventLoops nettyEventLoops = (NettyEventLoops) eventLoops;
        assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(NioSocketChannel.class);
        assertThat(nettyEventLoops.getSize()).isEqualTo(4);
    }
}
