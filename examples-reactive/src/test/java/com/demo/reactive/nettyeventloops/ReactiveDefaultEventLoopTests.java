package com.demo.reactive.nettyeventloops;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.reactive.nettyeventloops.entity.MovieDocument;
import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactiveDefaultEventLoopTests extends ReactiveNettyEventLoopsAerospikeDemoApplicationTest {

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
            .name("Inception")
            .description("A mind-bending thriller")
            .rating(8.8)
            .likes(100_000)
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
    void eventLoopsAreNettyBasedWithAutoSelectedTransport() {
        assertThat(eventLoops).isInstanceOf(NettyEventLoops.class);

        NettyEventLoops nettyEventLoops = (NettyEventLoops) eventLoops;
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (os.contains("nux") && Epoll.isAvailable()) {
            assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(EpollSocketChannel.class);
        } else if (os.contains("mac") && KQueue.isAvailable()) {
            assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(KQueueSocketChannel.class);
        } else {
            assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(NioSocketChannel.class);
        }
    }
}
