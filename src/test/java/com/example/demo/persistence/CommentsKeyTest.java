package com.example.demo.persistence;

import com.example.demo.persistence.compositeprimarykey.CommentsKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentsKeyTest {

    @Test
    void convertToCommentsKey() {
        CommentsKey actual = CommentsKey.StringToCommentsKeyConverter.INSTANCE.convert("comments::8888::9999");

        assertThat(actual).isEqualTo(new CommentsKey(8888, 9999));
    }

    @Test
    void convertToString() {
        String actual = CommentsKey.CommentsKeyToStringConverter.INSTANCE.convert(new CommentsKey(8888, 9999));

        assertThat(actual).isEqualTo("comments::8888::9999");
    }

    @Test
    void convertToCommentsKey_failsWithExceptionWhenInvalidString() {
        assertThatThrownBy(() -> CommentsKey.StringToCommentsKeyConverter.INSTANCE.convert("foobar"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Key can not be parsed: foobar");
    }
}