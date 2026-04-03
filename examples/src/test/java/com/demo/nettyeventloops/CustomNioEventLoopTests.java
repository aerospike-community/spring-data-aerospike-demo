package com.demo.nettyeventloops;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.nettyeventloops.entity.MovieDocument;
import com.demo.nettyeventloops.repository.MovieRepository;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "demo.eventloop.type=custom-nio")
public class CustomNioEventLoopTests extends NettyEventLoopsAerospikeDemoApplicationTest {

    @Autowired
    MovieRepository repository;

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
        repository.save(movie);
        assertThat(repository.findById(id)).hasValue(movie);

        repository.deleteById(id);
        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    void eventLoopsUseNioWithCustomThreadCount() {
        assertThat(eventLoops).isInstanceOf(NettyEventLoops.class);

        NettyEventLoops nettyEventLoops = (NettyEventLoops) eventLoops;
        assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(NioSocketChannel.class);
        assertThat(nettyEventLoops.getSize()).isEqualTo(4);
    }
}
