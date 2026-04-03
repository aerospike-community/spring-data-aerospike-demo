package com.demo.nettyeventloops;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.nettyeventloops.entity.MovieDocument;
import com.demo.nettyeventloops.repository.MovieRepository;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledOnOs(OS.LINUX)
@EnabledIf(value = "isIoUringAvailable", disabledReason = "io_uring is not available on this system")
@TestPropertySource(properties = "demo.eventloop.type=iouring")
public class IoUringEventLoopTests extends NettyEventLoopsAerospikeDemoApplicationTest {

    @Autowired
    MovieRepository repository;

    @Autowired
    EventLoops eventLoops;

    String id;
    MovieDocument movie;

    static boolean isIoUringAvailable() {
        try {
            return IoUring.isAvailable();
        } catch (Throwable e) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID().toString();
        movie = MovieDocument.builder()
            .id(id)
            .name("Dune")
            .description("A journey beyond imagination")
            .rating(8.0)
            .likes(150_000)
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
    void eventLoopsUseIoUring() {
        assertThat(eventLoops).isInstanceOf(NettyEventLoops.class);

        NettyEventLoops nettyEventLoops = (NettyEventLoops) eventLoops;
        assertThat(nettyEventLoops.getSocketChannelClass()).isEqualTo(IoUringSocketChannel.class);
    }
}
